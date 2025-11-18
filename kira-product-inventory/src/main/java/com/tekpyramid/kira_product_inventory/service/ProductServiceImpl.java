package com.tekpyramid.kira_product_inventory.service;

import com.tekpyramid.kira_product_inventory.dto.ProductDto;
import com.tekpyramid.kira_product_inventory.dto.ResponseDet;
import com.tekpyramid.kira_product_inventory.dto.VendorDto;
import com.tekpyramid.kira_product_inventory.entity.Category;
import com.tekpyramid.kira_product_inventory.entity.Inventory;
import com.tekpyramid.kira_product_inventory.entity.Product;
import com.tekpyramid.kira_product_inventory.exception.ResourceNotFoundException;
import com.tekpyramid.kira_product_inventory.exception.UnauthorizedAccessException;
import com.tekpyramid.kira_product_inventory.exception.VendorCurrentlyInactive;
import com.tekpyramid.kira_product_inventory.repository.CategoryRepository;
import com.tekpyramid.kira_product_inventory.repository.InventoryRepository;
import com.tekpyramid.kira_product_inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CategoryRepository categoryRepository;
    private final  MongoTemplate mongoTemplate;




    // ---------------- create ----------------
//    @Override
//    public ResponseDet createProduct(ProductDto productDto, String vendorId) {
//        try {
//            // TODO: call vendor/user service via Feign to check vendor status
//            VendorDto vendorDto = new VendorDto();
//            vendorDto.setStatus(true); // demo: assume active
//            Category category = categoryRepository.findByCategoryId(productDto.getCategoryId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategoryId()));
//            if (!vendorDto.isStatus()) {
//                throw new VendorCurrentlyInactive("Vendor Currently Inactive");
//            }
//            // enforce vendorId from header
//            productDto.setVendorId(vendorId);
//
//            Product product = dtoToEntity(productDto);
//            product.setProductId(new ObjectId().toHexString());
//            product.setVendorId(vendorId);
//
//            Product saved = productRepository.save(product);
//            if(productDto.getQuantity() != null && productDto.getQuantity() >= 1){
//
//                Inventory inventory = new Inventory();
//                inventory.setProductId(saved.getProductId());
//                inventory.setQuantity(productDto.getQuantity());
//                inventory.setVendorId(saved.getVendorId());
//                Inventory savedInventory = inventoryRepository.save(inventory);
//            } else {
//                throw new RuntimeException("Quantity should not be empty");
//            }
//            List<String> productIds = new ArrayList<>();
//            productIds.add(saved.getProductId());
//
//
//            List<String> productIdsTemp = category.getProductIds();
//
//// handle null case safely
//            if (productIdsTemp == null) {
//                productIdsTemp = new ArrayList<>();
//            }
//
//            productIdsTemp.add(saved.getProductId());
//            category.setProductIds(productIdsTemp);
//            categoryRepository.save(category);
//
//
//
//            ProductDto respDto = entityToDto(saved);
//            return new ResponseDet("Product and Inventory created successfully", false, HttpStatus.CREATED, respDto);
//
//        } catch (VendorCurrentlyInactive ex) {
//            return new ResponseDet(ex.getMessage(), true, HttpStatus.FORBIDDEN, null);
//        } catch (Exception ex) {
//            return new ResponseDet("Failed to create product: " + ex.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR, null);
//        }
//    }

@Override
public ResponseDet createProduct(ProductDto productDto, String vendorId) {
    // basic validations
    if (productDto == null) {
        log.warn("createProduct - payload is null");
        return new ResponseDet("Product payload is required", true, HttpStatus.BAD_REQUEST, null);
    }
    if (vendorId == null || vendorId.isBlank()) {
        log.warn("createProduct - vendorId header missing");
        return new ResponseDet("vendorId is required", true, HttpStatus.BAD_REQUEST, null);
    }
    if (productDto.getName() == null || productDto.getName().isBlank()) {
        log.warn("createProduct - product name missing");
        return new ResponseDet("Product name is required", true, HttpStatus.BAD_REQUEST, null);
    }
    if (productDto.getCategoryId() == null || productDto.getCategoryId().isBlank()) {
        log.warn("createProduct - categoryId missing");
        return new ResponseDet("categoryId is required", true, HttpStatus.BAD_REQUEST, null);
    }

    try {
        // TODO: call vendor/user service via Feign to check vendor status
        VendorDto vendorDto = new VendorDto();
        vendorDto.setStatus(true); // demo: assume active

        if (!vendorDto.isStatus()) {
            throw new VendorCurrentlyInactive("Vendor Currently Inactive");
        }

        // enforce vendorId from header
        productDto.setVendorId(vendorId);

        // 1) Ensure category exists
        Category category = categoryRepository.findByCategoryId(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategoryId()));

        // 2) Check duplicate product for same vendor (use mongoTemplate to be robust)
        Query dupQuery = Query.query(
                Criteria.where("vendorId").is(vendorId)
                        .and("name").is(productDto.getName().trim())
        );
        Product existing = mongoTemplate.findOne(dupQuery, Product.class);
        if (existing != null) {
            log.info("createProduct - duplicate product name '{}' for vendor {}", productDto.getName(), vendorId);
            return new ResponseDet("Product with same name already exists for this vendor", true, HttpStatus.CONFLICT, null);
        }

        // 3) Create product entity
        Product product = dtoToEntity(productDto);
        product.setProductId(new ObjectId().toHexString());
        product.setVendorId(vendorId);
        // ensure name trimmed
        product.setName(product.getName() == null ? null : product.getName().trim());

        Product saved = productRepository.save(product);
        log.info("createProduct - product saved: id={}, vendor={}", saved.getProductId(), saved.getVendorId());

        // 4) Create inventory (quantity must be present and >= 1)
        if (productDto.getQuantity() == null || productDto.getQuantity() < 1) {
            // rollback product save is not performed here (best-effort). You can enable transaction if desired.
            log.warn("createProduct - invalid/empty quantity for product {}", saved.getProductId());
            return new ResponseDet("Quantity must be provided and >= 1", true, HttpStatus.BAD_REQUEST, null);
        }

        Inventory inventory = new Inventory();
        inventory.setProductId(saved.getProductId());
        inventory.setQuantity(productDto.getQuantity());
        inventory.setVendorId(saved.getVendorId());
        Inventory savedInventory = inventoryRepository.save(inventory);
        log.info("createProduct - inventory created for product {} qty={}", savedInventory.getProductId(), savedInventory.getQuantity());

        // 5) Add productId to category (handle null productIds)
        List<String> productIdsTemp = category.getProductIds();
        if (productIdsTemp == null) {
            productIdsTemp = new ArrayList<>();
        }
        productIdsTemp.add(saved.getProductId());
        category.setProductIds(productIdsTemp);
        categoryRepository.save(category);
        log.info("createProduct - category {} updated with product {}", category.getCategoryId(), saved.getProductId());

        ProductDto respDto = entityToDto(saved);
        return new ResponseDet("Product and Inventory created successfully", false, HttpStatus.CREATED, respDto);

    } catch (VendorCurrentlyInactive ex) {
        log.warn("createProduct - vendor inactive: {}", vendorId);
        return new ResponseDet(ex.getMessage(), true, HttpStatus.FORBIDDEN, null);

    } catch (ResourceNotFoundException rnfe) {
        log.info("createProduct - related resource not found: {}", rnfe.getMessage());
        return new ResponseDet(rnfe.getMessage(), true, HttpStatus.NOT_FOUND, null);

    } catch (Exception ex) {
        log.error("createProduct - unexpected error: {}", ex.getMessage(), ex);
        return new ResponseDet("Failed to create product: " + ex.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR, null);
    }
}


    //---------------Craete produts------------------

    @Override
    @Transactional
    public ResponseDet createProducts(List<ProductDto> productDtos, String vendorId) {
        // Basic validations
        if (vendorId == null || vendorId.isBlank()) {
            log.warn("createProducts - vendorId missing");
            return new ResponseDet("vendorId is required", true, HttpStatus.BAD_REQUEST, null);
        }
        if (productDtos == null || productDtos.isEmpty()) {
            log.warn("createProducts - no products provided");
            return new ResponseDet("No products provided", true, HttpStatus.BAD_REQUEST, null);
        }

        try {
            // Demo vendor check (replace with feign call)
            VendorDto vendorDto = new VendorDto();
            vendorDto.setStatus(true);
            if (!vendorDto.isStatus()) {
                throw new VendorCurrentlyInactive("Vendor currently inactive");
            }

            // Validate each DTO and collect names
            List<String> missingFields = new ArrayList<>();
            List<String> incomingNames = new ArrayList<>();
            for (int i = 0; i < productDtos.size(); i++) {
                ProductDto dto = productDtos.get(i);
                String ctx = "index=" + i + (dto == null ? "" : ",name=" + dto.getName());
                if (dto == null) {
                    missingFields.add("ProductDto at " + ctx + " is null");
                    continue;
                }
                if (dto.getName() == null || dto.getName().isBlank()) {
                    missingFields.add("Product name required for " + ctx);
                }
                if (dto.getCategoryId() == null || dto.getCategoryId().isBlank()) {
                    missingFields.add("categoryId required for " + ctx);
                }
                if (dto.getQuantity() == null || dto.getQuantity() < 1) {
                    missingFields.add("quantity >=1 required for " + ctx);
                }
                // enforce vendorId from header into DTO for consistency
                dto.setVendorId(vendorId);

                // collect normalized name (trim + lower) for duplicate checks
                if (dto.getName() != null) {
                    incomingNames.add(dto.getName().trim().toLowerCase());
                }
            }

            if (!missingFields.isEmpty()) {
                log.warn("createProducts - validation errors: {}", missingFields);
                return new ResponseDet("Validation errors", true, HttpStatus.BAD_REQUEST, missingFields);
            }

            // 1) Check duplicates within incoming request (case-insensitive)
            Map<String, Long> nameCounts = incomingNames.stream()
                    .collect(Collectors.groupingBy(n -> n, Collectors.counting()));
            List<String> dupInRequest = nameCounts.entrySet().stream()
                    .filter(e -> e.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (!dupInRequest.isEmpty()) {
                log.info("createProducts - duplicate names found in request for vendor {}: {}", vendorId, dupInRequest);
                return new ResponseDet("Duplicate product names in request", true, HttpStatus.CONFLICT, dupInRequest);
            }

            // 2) Check duplicates against DB for same vendor (case-insensitive)
            // Build set of original (trimmed) names (preserve original casing not needed).
            Set<String> nameSet = productDtos.stream()
                    .map(p -> p.getName().trim())
                    .collect(Collectors.toSet());

            // Use case-insensitive search by building regex criteria (or store lowercase name field in DB ideally)
            Query existingQuery = new Query();
            existingQuery.addCriteria(Criteria.where("vendorId").is(vendorId)
                    .andOperator(
                            Criteria.where("name").in(nameSet) // this is case-sensitive; below we'll filter case-insensitively if needed
                    ));
            // Fetch possible matches (we will compare case-insensitively in Java)
            List<Product> possibleMatches = mongoTemplate.find(existingQuery, Product.class);

            // filter matches case-insensitively
            Set<String> conflictingNames = new HashSet<>();
            Set<String> lowerNameSet = nameSet.stream().map(String::toLowerCase).collect(Collectors.toSet());
            for (Product p : possibleMatches) {
                if (p.getName() != null && lowerNameSet.contains(p.getName().trim().toLowerCase())) {
                    conflictingNames.add(p.getName().trim());
                }
            }

            if (!conflictingNames.isEmpty()) {
                log.info("createProducts - vendor {} already has products with names {}", vendorId, conflictingNames);
                return new ResponseDet("Some products already exist for this vendor", true, HttpStatus.CONFLICT, conflictingNames);
            }

            // 3) Validate categories exist and load them once (collect unique categoryIds)
            Set<String> categoryIds = productDtos.stream()
                    .map(ProductDto::getCategoryId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Map<String, Category> categoryMap = new HashMap<>();
            for (String cid : categoryIds) {
                Category cat = categoryRepository.findByCategoryId(cid)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + cid));
                categoryMap.put(cid, cat);
            }

            // 4) Prepare entities
            List<Product> productsToSave = new ArrayList<>();
            List<Inventory> inventoriesToSave = new ArrayList<>();
            Map<String, List<String>> categoryToNewProdIds = new HashMap<>();

            for (ProductDto dto : productDtos) {
                Product product = dtoToEntity(dto);
                String newProdId = new ObjectId().toHexString();
                product.setProductId(newProdId);
                product.setVendorId(vendorId);
                // normalize product name trim
                product.setName(product.getName() == null ? null : product.getName().trim());

                productsToSave.add(product);

                Inventory inv = new Inventory();
                inv.setInventoryId(new ObjectId().toHexString());
                inv.setProductId(newProdId);
                inv.setQuantity(dto.getQuantity());
                inv.setVendorId(vendorId);
                inventoriesToSave.add(inv);

                String cid = dto.getCategoryId();
                categoryToNewProdIds.computeIfAbsent(cid, k -> new ArrayList<>()).add(newProdId);
            }

            // 5) Save all products and inventories (transactional -> will roll back on exception)
            List<Product> savedProducts = productRepository.saveAll(productsToSave);
            List<Inventory> savedInventories = inventoryRepository.saveAll(inventoriesToSave);

            // 6) Update categories
            for (Map.Entry<String, List<String>> e : categoryToNewProdIds.entrySet()) {
                String cid = e.getKey();
                List<String> newIds = e.getValue();
                Category cat = categoryMap.get(cid);
                if (cat == null) {
                    throw new ResourceNotFoundException("Category not loaded: " + cid);
                }
                List<String> existing = cat.getProductIds();
                if (existing == null) existing = new ArrayList<>();
                existing.addAll(newIds);
                cat.setProductIds(existing);
                categoryRepository.save(cat);
            }

            // 7) Build response DTOs
            List<ProductDto> respDtos = savedProducts.stream()
                    .map(this::entityToDto)
                    .collect(Collectors.toList());

            return new ResponseDet("Products, Inventories and Categories updated", false, HttpStatus.CREATED, respDtos);

        } catch (VendorCurrentlyInactive ex) {
            log.warn("createProducts - vendor inactive: {}", vendorId);
            return new ResponseDet(ex.getMessage(), true, HttpStatus.FORBIDDEN, null);
        } catch (ResourceNotFoundException rnfe) {
            log.info("createProducts - resource not found: {}", rnfe.getMessage());
            return new ResponseDet(rnfe.getMessage(), true, HttpStatus.NOT_FOUND, null);
        } catch (Exception ex) {
            log.error("createProducts - unexpected error: {}", ex.getMessage(), ex);
            return new ResponseDet("Bulk create failed: " + ex.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }



    // ---------------- update ----------------
    @Override
    @Transactional              // require MongoTransactionManager bean + replica-set for true atomicity
    public ResponseDet updateProduct(ProductDto productDto, String vendorId) {
        try {
            if (productDto.getProductId() == null || productDto.getProductId().isBlank()) {
                return new ResponseDet("productId is required for update", true, HttpStatus.BAD_REQUEST, null);
            }

            Product existing = productRepository.findById(productDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productDto.getProductId()));

            if (!existing.getVendorId().equals(vendorId)) {
                throw new UnauthorizedAccessException("You are not authorized to update this product");
            }

            String oldCategoryId = existing.getCategoryId();
            String newCategoryId = productDto.getCategoryId();

            // If category changed (and newCategoryId is provided)
            if (newCategoryId != null && !Objects.equals(newCategoryId, oldCategoryId)) {

                // Load newCategory (target)
                Category newCategory = categoryRepository.findByCategoryId(newCategoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + newCategoryId));

                // Load oldCategory (only if oldCategoryId exists)
                Category oldCategory = null;
                if (oldCategoryId != null && !oldCategoryId.isBlank()) {
                    oldCategory = categoryRepository.findByCategoryId(oldCategoryId)
                            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + oldCategoryId));
                }

                // Add productId to new category if not already present
                List<String> newProdIds = newCategory.getProductIds();
                if (newProdIds == null) newProdIds = new ArrayList<>();
                if (!newProdIds.contains(existing.getProductId())) {
                    newProdIds.add(existing.getProductId());
                }
                newCategory.setProductIds(newProdIds);

                // Remove productId from old category if present
                if (oldCategory != null) {
                    List<String> oldProdIds = oldCategory.getProductIds();
                    if (oldProdIds != null && oldProdIds.remove(existing.getProductId())) {
                        oldCategory.setProductIds(oldProdIds);
                    }
                }

                // Save both categories (if oldCategory is null only newCategory saved)
                if (oldCategory != null) {
                    categoryRepository.saveAll(Arrays.asList(newCategory, oldCategory));
                } else {
                    categoryRepository.save(newCategory);
                }

                // finally update the product's categoryId (below, as part of partial update)
            }

            if(productDto.getQuantity() != null && productDto.getQuantity() >= 1){
                Inventory inventory = new Inventory();
                Inventory existingInventory = inventoryRepository.findByProductId(existing.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Inventory not found for product: " + existing.getProductId()
                        ));
                if (!existingInventory.getVendorId().equals(vendorId)) {
                    throw new UnauthorizedAccessException("You are not authorized to update this inventory");
                }
                existingInventory.setQuantity(productDto.getQuantity());
                inventoryRepository.save(existingInventory);
            }

            // partial update semantics: if dto field is null, keep existing value
            existing.setName(productDto.getName() == null ? existing.getName() : productDto.getName());
            existing.setDescription(productDto.getDescription() == null ? existing.getDescription() : productDto.getDescription());
            existing.setCategoryId(productDto.getCategoryId() == null ? existing.getCategoryId() : productDto.getCategoryId());
            existing.setPrice(productDto.getPrice() == null ? existing.getPrice() : productDto.getPrice());

//            // NOTE: align on the correct field name for images in your entity (images vs image)
//            // I'm using `setImages` if your entity has `images` - change if your entity uses `setImage`.
//            if (productDto.getImage() != null) {
//                existing.setImage(productDto.getImage());
//            }

            Product updated = productRepository.save(existing);
            return new ResponseDet("Product updated successfully", false, HttpStatus.OK, entityToDto(updated));

        } catch (ResourceNotFoundException ex) {
            return new ResponseDet(ex.getMessage(), true, HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedAccessException ex) {
            return new ResponseDet(ex.getMessage(), true, HttpStatus.FORBIDDEN, null);
        } catch (Exception ex) {
            return new ResponseDet("Failed to update product: " + ex.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    // ---------- mapping helpers ----------
    private Product dtoToEntity(ProductDto dto) {
        if (dto == null) return null;
        Product p = new Product();
        p.setProductId(dto.getProductId());
        p.setVendorId(dto.getVendorId());
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setCategoryId(dto.getCategoryId());
        p.setPrice(dto.getPrice() == null ? 0.0 : dto.getPrice());
        p.setVendorSatatus(dto.isVendorSatatus());
        return p;
    }

    private ProductDto entityToDto(Product entity) {
        if (entity == null) return null;
        return ProductDto.builder()
                .productId(entity.getProductId())
                .vendorId(entity.getVendorId())
                .name(entity.getName())
                .description(entity.getDescription())
                .categoryId(entity.getCategoryId())
                .price(entity.getPrice())
                .vendorSatatus(entity.isVendorSatatus())
                .build();
    }

//    Aravind ================================


    // ---------------------------------------------------------
    // Convert Product → ProductDto with BaseEntityDto fields
    // ---------------------------------------------------------
    private ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        BeanUtils.copyProperties(product, dto);

        // BaseEntity fields mapping
        dto.setCreatedAt(product.getCreatedAt() != null ? product.getCreatedAt().toString() : null);
        dto.setUpdatedAt(product.getUpdatedAt() != null ? product.getUpdatedAt().toString() : null);
        dto.setCreatedBy(product.getCreatedBy());
        dto.setUpdatedBy(product.getUpdatedBy());

        return dto;
    }

    // ---------------------------------------------------------
    // API 1 → Get Products by Vendor (Paged)
    // ---------------------------------------------------------
    @Override
    public ResponseDet getProductsByVendorId(String vendorId, Pageable pageable) {

        // Fetch paged vendor products
        List<Product> products = productRepository.findByVendorId(vendorId, pageable)
                .stream().collect(Collectors.toList());

        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found for vendor: " + vendorId);
        }

        List<ProductDto> list = products.stream().map(product -> {
            ProductDto dto = toDto(product);

            // Fetch quantity
            Inventory inv = inventoryRepository.findByProductIdAndVendorId(
                    product.getProductId(), vendorId
            );
            dto.setQuantity(inv != null ? inv.getQuantity() : null);

            return dto;
        }).collect(Collectors.toList());

        return new ResponseDet("Products fetched successfully", false, HttpStatus.OK, list);
    }

    // ---------------------------------------------------------
    // API 2 → Fetch All Products (Paged)
    // ---------------------------------------------------------
    @Override
    public ResponseDet getAllProducts(Pageable pageable) {

        try {
            List<ProductDto> list = productRepository.findAll(pageable)
                    .stream()
                    .map(product -> {

                        ProductDto dto = toDto(product);

                        try {
                            Inventory inv = inventoryRepository.findByProductIdAndVendorId(
                                    product.getProductId(), product.getVendorId()
                            );
                            dto.setQuantity(inv != null ? inv.getQuantity() : null);
                        } catch (Exception e) {
                            dto.setQuantity(null);
                        }

                        return dto;
                    })
                    .collect(Collectors.toList());

            return new ResponseDet("All products fetched successfully", false, HttpStatus.OK, list);

        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch product list: " + ex.getMessage());
        }
    }

    // ---------------------------------------------------------
    // API 3 → Get Product by ID
    // ---------------------------------------------------------
    @Override
    public ResponseDet getProductById(String productId) {

        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Product not found with ID: " + productId)
                    );

            ProductDto dto = toDto(product);

            try {
                Inventory inv = inventoryRepository.findByProductIdAndVendorId(
                        productId, product.getVendorId()
                );
                dto.setQuantity(inv != null ? inv.getQuantity() : null);

            } catch (Exception e) {
                dto.setQuantity(null);
            }

            return new ResponseDet("Product fetched successfully", false, HttpStatus.OK, dto);

        } catch (ResourceNotFoundException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch product: " + ex.getMessage());
        }
    }

    // ---------------------------------------------------------
    // API 4 → Get Products by Category (Paged)
    // ---------------------------------------------------------
    @Override
    public ResponseDet getProductsByCategoryId(String categoryId, Pageable pageable) {

        try {
            boolean categoryExists = productRepository.findByCategoryId(categoryId).size() > 0;

            if (!categoryExists) {
                throw new ResourceNotFoundException("No products found for category: " + categoryId);
            }

            List<ProductDto> list = productRepository.findByCategoryId(categoryId, pageable)
                    .stream()
                    .map(product -> {

                        ProductDto dto = toDto(product);

                        try {
                            Inventory inv = inventoryRepository.findByProductIdAndVendorId(
                                    product.getProductId(), product.getVendorId()
                            );
                            dto.setQuantity(inv != null ? inv.getQuantity() : null);
                        } catch (Exception e) {
                            dto.setQuantity(null);
                        }

                        return dto;
                    })
                    .collect(Collectors.toList());

            if (list.isEmpty()) {
                throw new ResourceNotFoundException("No products found for category: " + categoryId);
            }

            return new ResponseDet(
                    "Products fetched successfully for category: " + categoryId,
                    false,
                    HttpStatus.OK,
                    list
            );

        } catch (ResourceNotFoundException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch category products: " + ex.getMessage());
        }
    }

    // ---------------------------------------------------------
    // API 5 → Search Products by Name
    // ---------------------------------------------------------
    @Override
    public ResponseDet searchProductsByName(String keyword, Pageable pageable) {

        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return new ResponseDet("Search keyword cannot be empty",
                        true, HttpStatus.BAD_REQUEST, null);
            }

            List<ProductDto> list = productRepository
                    .findByNameContainingIgnoreCase(keyword.trim(), pageable)
                    .stream()
                    .map(product -> {

                        ProductDto dto = toDto(product);

                        try {
                            Inventory inv = inventoryRepository.findByProductIdAndVendorId(
                                    product.getProductId(), product.getVendorId()
                            );
                            dto.setQuantity(inv != null ? inv.getQuantity() : null);
                        } catch (Exception e) {
                            dto.setQuantity(null);
                        }

                        return dto;
                    })
                    .collect(Collectors.toList());

            if (list.isEmpty()) {
                throw new ResourceNotFoundException("No products found for: " + keyword);
            }

            return new ResponseDet(
                    "Products fetched successfully for: " + keyword,
                    false,
                    HttpStatus.OK,
                    list
            );

        } catch (ResourceNotFoundException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new RuntimeException("Search failed: " + ex.getMessage());
        }
    }

    // ---------------------------------------------------------
    // API 6 → Delete Product by ID
    // ---------------------------------------------------------
    @Override
    public ResponseDet deleteProductById(String productId) {

        try {
            ObjectId objId;

            try {
                objId = new ObjectId(productId);
            } catch (Exception e) {
                return new ResponseDet("Invalid product ID format",
                        true, HttpStatus.BAD_REQUEST, null);
            }

            Product product = mongoTemplate.findById(objId, Product.class);

            if (product == null) {
                throw new ResourceNotFoundException("Product not found: " + productId);
            }

            // delete inventory
            try {
                inventoryRepository.deleteByProductId(productId);
            } catch (Exception e) {}

            // delete product
            mongoTemplate.remove(
                    Query.query(Criteria.where("_id").is(objId)),
                    Product.class
            );

            return new ResponseDet(
                    "Product deleted successfully",
                    false,
                    HttpStatus.OK,
                    "Deleted Product ID: " + productId
            );

        } catch (ResourceNotFoundException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new RuntimeException("Delete failed: " + ex.getMessage());
        }
    }

    // ---------------------------------------------------------
    // API 7 → Delete All Products for a Vendor
    // ---------------------------------------------------------
    @Override
    public ResponseDet deleteProductsByVendorId(String vendorId) {

        try {
            if (vendorId == null || vendorId.trim().isEmpty()) {
                return new ResponseDet("Vendor ID cannot be empty",
                        true, HttpStatus.BAD_REQUEST, null);
            }

            List<Product> products = productRepository.findByVendorId(vendorId);

            if (products.isEmpty()) {
                throw new ResourceNotFoundException("No products found for vendor: " + vendorId);
            }

            int deleteCount = 0;

            for (Product product : products) {

                String productId = product.getProductId();

                try {
                    inventoryRepository.deleteByProductId(productId);
                } catch (Exception e) {
                }

                try {
                    ObjectId objId = new ObjectId(productId);

                    mongoTemplate.remove(
                            Query.query(Criteria.where("_id").is(objId)),
                            Product.class
                    );

                    deleteCount++;

                } catch (Exception e) {
                    return new ResponseDet(
                            "Invalid product ID format in vendor list: " + productId,
                            true, HttpStatus.BAD_REQUEST, null
                    );
                }
            }

            return new ResponseDet(
                    "All products deleted for vendor: " + vendorId,
                    false,
                    HttpStatus.OK,
                    "Deleted Count: " + deleteCount
            );

        } catch (ResourceNotFoundException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new RuntimeException("Vendor product deletion failed: " + ex.getMessage());
        }
    }


}
