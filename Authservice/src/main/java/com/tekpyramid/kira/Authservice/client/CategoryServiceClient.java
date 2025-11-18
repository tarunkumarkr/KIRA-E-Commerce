package com.tekpyramid.kira.Authservice.client;


import com.tekpyramid.kira.Authservice.dto.CategoryRequestDTO;
import com.tekpyramid.kira.Authservice.dto.ResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "PRODUCT-INVENTORY-SERVICE",
        contextId = "categoryClient",
        path = "/api/categories"
)
public interface CategoryServiceClient {

    // CREATE SINGLE CATEGORY
    @PostMapping("/save")
    ResponseDTO createCategory(@RequestBody CategoryRequestDTO categoryDto);

    // BULK CREATE
    @PostMapping("/bulk-save")
    ResponseDTO createCategories(@RequestBody List<CategoryRequestDTO> categories);

    // UPDATE CATEGORY
    @PutMapping("/{categoryId}")
    ResponseDTO updateCategory(
            @PathVariable("categoryId") String categoryId,
            @RequestBody CategoryRequestDTO categoryDto
    );

    // DELETE CATEGORY
    @DeleteMapping("/category/{categoryId}")
    ResponseDTO deleteCategoryById(
            @PathVariable("categoryId") String categoryId
    );

    // GET ALL CATEGORIES (Paginated)
    @GetMapping
    ResponseDTO getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    );
}