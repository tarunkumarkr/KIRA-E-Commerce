package com.tekpyramid.kira_product_inventory.repository;


import com.tekpyramid.kira_product_inventory.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    Optional<Category> findByName(String name);

    Optional<Category> findByCategoryId(String categoryId);

}
