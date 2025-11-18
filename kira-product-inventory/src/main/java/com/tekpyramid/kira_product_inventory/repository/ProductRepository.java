package com.tekpyramid.kira_product_inventory.repository;

import com.tekpyramid.kira_product_inventory.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {

    // vendor products (paged)
    Page<Product> findByVendorId(String vendorId, Pageable pageable);

    // get all products paged
    Page<Product> findAll(Pageable pageable);

    // find products by single categoryId
    Page<Product> findByCategoryId(String categoryId, Pageable pageable);

    // vendor's raw product list
    List<Product> findByVendorId(String vendorId);

    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Product> findByCategoryId(String categoryId);



}
