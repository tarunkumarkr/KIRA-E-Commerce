package com.tekpyramid.kira.Authservice.controller;


import com.tekpyramid.kira.Authservice.client.CategoryServiceClient;
import com.tekpyramid.kira.Authservice.dto.CategoryRequestDTO;
import com.tekpyramid.kira.Authservice.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth/categories")
@RequiredArgsConstructor
public class AuthCategoryController {
    private final CategoryServiceClient categoryServiceClient;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ResponseDTO> createCategory(@RequestBody CategoryRequestDTO dto) {

        ResponseDTO response = categoryServiceClient.createCategory(dto);

        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/bulk-save")
    public ResponseEntity<ResponseDTO> createCategoriesBulk(@RequestBody List<CategoryRequestDTO> categories, @RequestHeader("X-User-ID") String userId, @RequestHeader(value = "X-User-Role", required = false) String role) {

        ResponseDTO response = categoryServiceClient.createCategories(categories);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{categoryId}")
    public ResponseEntity<ResponseDTO> updateCategory(@PathVariable String categoryId, @RequestBody CategoryRequestDTO requestDTO, @RequestHeader("X-User-ID") String userId, @RequestHeader(value = "X-User-Role", required = false) String role) {
        System.out.println("Updating category with ID: " + categoryId);
        ResponseDTO response = categoryServiceClient.updateCategory(categoryId, requestDTO);

        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<ResponseDTO> deleteCategoryById(@PathVariable("categoryId") String categoryId) {

        ResponseDTO response = categoryServiceClient.deleteCategoryById(categoryId);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}
