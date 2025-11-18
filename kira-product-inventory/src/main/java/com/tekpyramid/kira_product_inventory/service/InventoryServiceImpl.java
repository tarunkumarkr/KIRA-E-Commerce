package com.tekpyramid.kira_product_inventory.service;

import com.tekpyramid.kira_product_inventory.dto.*;
import com.tekpyramid.kira_product_inventory.entity.Inventory;
import com.tekpyramid.kira_product_inventory.entity.Product;
import com.tekpyramid.kira_product_inventory.exception.ResourceNotFoundException;
import com.tekpyramid.kira_product_inventory.repository.InventoryRepository;
import com.tekpyramid.kira_product_inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public ResponseDet checkStock(List<StockCheckItem> items) {
        if (items == null || items.isEmpty()) {
            return new ResponseDet("Request must contain at least one product", true, HttpStatus.BAD_REQUEST, null);
        }

        List<StockCheckResult> results = new ArrayList<>();

        for (StockCheckItem item : items) {
            String productIdStr = item == null ? null : item.getProductId();
            int requestedQty = item == null || item.getQuantity() == null ? 0 : item.getQuantity();

            // Basic validation
            if (productIdStr == null || productIdStr.isBlank()) {
                log.warn("checkStock - invalid productId: '{}'", productIdStr);
                return new ResponseDet("Invalid productId", true, HttpStatus.BAD_REQUEST, null);
            }

            ObjectId oid;
            try {
                oid = new ObjectId(productIdStr);
            } catch (IllegalArgumentException ex) {
                log.warn("checkStock - invalid ObjectId for productId: {}", productIdStr, ex);
                return new ResponseDet("Product not found: " + productIdStr, true, HttpStatus.NOT_FOUND, null);
            }

            try {
                // fetch product using mongoTemplate (products._id is ObjectId)
                Query q = new Query(Criteria.where("_id").is(oid));
                Product product = mongoTemplate.findOne(q, Product.class);
                if (product == null) {
                    log.info("checkStock - product not found for id: {}", productIdStr);
                    return new ResponseDet("Product not found: " + productIdStr, true, HttpStatus.NOT_FOUND, null);
                }

                // fetch inventory by productId string
                Optional<Inventory> invOpt = inventoryRepository.findByProductId(productIdStr);
                int availableQty = invOpt.map(Inventory::getQuantity).orElse(0);
                boolean isAvailable = requestedQty >= 1 && availableQty >= requestedQty;

                results.add(new StockCheckResult(productIdStr, requestedQty, availableQty, isAvailable));
            } catch (DataAccessException dae) {
                // DB level error
                log.error("checkStock - database error while checking product {}: {}", productIdStr, dae.getMessage(), dae);
                return new ResponseDet("Database error while checking stock", true, HttpStatus.INTERNAL_SERVER_ERROR, null);
            } catch (Exception ex) {
                log.error("checkStock - unexpected error for product {}: {}", productIdStr, ex.getMessage(), ex);
                return new ResponseDet("Unexpected error while checking stock", true, HttpStatus.INTERNAL_SERVER_ERROR, null);
            }
        }

        log.info("Stock results -> {}", results);
        return new ResponseDet("Stock check completed", false, HttpStatus.OK, results);
    }

    /**
     * Attempts to reduce inventory for each item. Per-item behavior:
     * - If product not found -> reduced=false, message="Product not found"
     * - If inventory not found -> reduced=false, message="Inventory not found"
     * - If insufficient -> reduced=false, message="Insufficient stock"
     * - If sufficient -> atomically decrement and return reduced=true and remaining
     */
    @Override
    public ResponseDet reduceStock(ReduceStockRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            return new ResponseDet("Request must contain items", true, HttpStatus.BAD_REQUEST, null);
        }

        List<ReduceStockResult> results = new ArrayList<>();

        for (ReduceStockItem item : request.getItems()) {
            String pid = item == null ? null : item.getProductId();
            int qty = item == null || item.getQuantity() == null ? 0 : item.getQuantity();

            // validation
            if (pid == null || pid.isBlank()) {
                log.warn("reduceStock - invalid productId '{}'", pid);
                results.add(new ReduceStockResult(pid, qty, 0, false, "Invalid productId"));
                continue;
            }
            if (qty < 1) {
                log.warn("reduceStock - invalid quantity {} for product {}", qty, pid);
                results.add(new ReduceStockResult(pid, qty, 0, false, "Invalid quantity"));
                continue;
            }

            ObjectId oid;
            try {
                oid = new ObjectId(pid);
            } catch (IllegalArgumentException e) {
                log.warn("reduceStock - invalid ObjectId '{}'", pid, e);
                results.add(new ReduceStockResult(pid, qty, 0, false, "Product not found"));
                continue;
            }

            try {
                // 1) Check product exists
                Query productQuery = new Query(Criteria.where("_id").is(oid));
                Product product = mongoTemplate.findOne(productQuery, Product.class);
                if (product == null) {
                    log.info("reduceStock - product not found for id {}", pid);
                    results.add(new ReduceStockResult(pid, qty, 0, false, "Product not found"));
                    continue;
                }

                // 2) Attempt atomic decrement on inventory document where product_id == pid and quantity >= qty
                Query invQuery = new Query(Criteria.where("product_id").is(pid).and("quantity").gte(qty));
                Update update = new Update().inc("quantity", -qty);
                FindAndModifyOptions opts = new FindAndModifyOptions().returnNew(true);

                Inventory updatedInv = mongoTemplate.findAndModify(invQuery, update, opts, Inventory.class);

                if (updatedInv == null) {
                    // Distinguish between inventory missing and insufficient stock
                    Optional<Inventory> invMaybe = inventoryRepository.findByProductId(pid);
                    if (invMaybe.isEmpty()) {
                        log.info("reduceStock - inventory not found for product {}", pid);
                        results.add(new ReduceStockResult(pid, qty, 0, false, "Inventory not found"));
                    } else {
                        int available = invMaybe.map(Inventory::getQuantity).orElse(0);
                        log.info("reduceStock - insufficient stock for product {}: requested={}, available={}", pid, qty, available);
                        results.add(new ReduceStockResult(pid, qty, available, false, "Insufficient stock"));
                    }
                } else {
                    log.info("reduceStock - reduced product {} by {} newQty={}", pid, qty, updatedInv.getQuantity());
                    results.add(new ReduceStockResult(pid, qty, updatedInv.getQuantity(), true, "Reduced"));
                }
            } catch (DataAccessException dae) {
                log.error("reduceStock - database error for product {}: {}", pid, dae.getMessage(), dae);
                results.add(new ReduceStockResult(pid, qty, 0, false, "Database error"));
            } catch (ResourceNotFoundException rnfe) {
                log.warn("reduceStock - resource not found for {}: {}", pid, rnfe.getMessage());
                results.add(new ReduceStockResult(pid, qty, 0, false, "Product not found"));
            } catch (Exception ex) {
                log.error("reduceStock - unexpected error for product {}: {}", pid, ex.getMessage(), ex);
                results.add(new ReduceStockResult(pid, qty, 0, false, "Unexpected error"));
            }
        }

        return new ResponseDet("Inventory reduce processed", false, HttpStatus.OK, results);
    }
}
