package com.tekpyramid.kira_product_inventory.controller;

import com.tekpyramid.kira_product_inventory.dto.CategoryDto;
import com.tekpyramid.kira_product_inventory.dto.ResponseDet;
import com.tekpyramid.kira_product_inventory.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/save")
    public ResponseEntity<ResponseDet> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        ResponseDet resp = categoryService.createCategory(categoryDto);
        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
    }

    // Bulk create categories
    @PostMapping("/bulk-save")
    public ResponseEntity<ResponseDet> createCategoriesBulk(
            @Valid @RequestBody List<@Valid CategoryDto> categories) {
        System.out.println("hi dude iam here");
        ResponseDet resp = categoryService.createCategories(categories);
        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ResponseDet> updateCategory(
            @PathVariable String categoryId,
            @Valid @RequestBody CategoryDto categoryDto) {
        categoryDto.setCategoryId(categoryId);
        ResponseDet resp = categoryService.updateCategory(categoryDto);
        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
    }

//    private final CategoryService categoryService;


    //  API 8
    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<ResponseDet> deleteCategoryById(
            @PathVariable String categoryId
    ) {
        return ResponseEntity.ok(categoryService.deleteCategoryById(categoryId));
    }

    // API 9 — Get all categories (with pagination)
    @GetMapping
    public ResponseEntity<ResponseDet> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(categoryService.getAllCategories(pageable));
    }

//    @GetMapping("/{categoryId}")
//    public ResponseEntity<ResponseDet> getCategory(@PathVariable String categoryId) {
//        ResponseDet resp = categoryService.getCategoryById(categoryId);
//        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
//    }
//
//    @GetMapping
//    public ResponseEntity<ResponseDet> getAllCategories(Pageable pageable) {
//        ResponseDet resp = categoryService.getAllCategories(pageable);
//        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
//    }
//
//    @DeleteMapping("/{categoryId}")
//    public ResponseEntity<ResponseDet> deleteCategory(@PathVariable String categoryId) {
//        ResponseDet resp = categoryService.deleteCategory(categoryId);
//        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
//    }
}

