package com.tekpyramid.kira_product_inventory.repository;

import com.tekpyramid.kira_product_inventory.entity.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends MongoRepository<Inventory, String> {
    Optional<Inventory> findByProductId(String productId);

    void deleteByProductId(String productId);

    Inventory findByProductIdAndVendorId(String productId, String vendorId);
}
