package com.tekpyramid.kira_product_inventory.controller;

import com.tekpyramid.kira_product_inventory.dto.ProductDto;
import com.tekpyramid.kira_product_inventory.dto.ResponseDet;
import com.tekpyramid.kira_product_inventory.dto.VendorDto;
import com.tekpyramid.kira_product_inventory.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;



//    public VendorDto fetchVendorDetails{
//
//        return vendorDto;
//    }


    // Create product — request validated by @Valid
    @PostMapping("/save")
    public ResponseEntity<ResponseDet> createProduct(
            @Valid @RequestBody ProductDto productDto,
            @RequestHeader("X-User-ID") String vendorId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        ResponseDet resp = productService.createProduct(productDto, vendorId);
        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
    }

    @PostMapping("/save-bulk")
    public ResponseEntity<ResponseDet> createProductsBulk(
            @Valid @RequestBody List<@Valid ProductDto> productDtos,
            @RequestHeader("X-User-ID") String vendorId) {

        ResponseDet resp = productService.createProducts(productDtos, vendorId);
        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
    }

    // Update product
    @PutMapping("/{productId}")
    public ResponseEntity<ResponseDet> updateProduct(
            @PathVariable String productId,
            @Valid @RequestBody ProductDto productDto,
            @RequestHeader("X-User-ID") String vendorId) {

        productDto.setProductId(productId); // ensure id is set on DTO
        ResponseDet resp = productService.updateProduct(productDto, vendorId);
        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
    }



//    =====================================================================

    // API 1
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<ResponseDet> getProductsByVendorId(
            @PathVariable String vendorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(productService.getProductsByVendorId(vendorId, pageable));
    }

    //API 2

    @GetMapping
    public ResponseEntity<ResponseDet> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

//API 3

    @GetMapping("/{productId}")
    public ResponseEntity<ResponseDet> getProductById(
            @PathVariable String productId
    ) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

// API 4

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseDet> getProductsByCategoryId(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(productService.getProductsByCategoryId(categoryId, pageable));
    }

// API 5

    @GetMapping("/search")
    public ResponseEntity<ResponseDet> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(
                productService.searchProductsByName(keyword, pageable)
        );
    }

//API 6

    @DeleteMapping("/{productId}")
    public ResponseEntity<ResponseDet> deleteProductById(
            @PathVariable String productId
    ) {
        return ResponseEntity.ok(productService.deleteProductById(productId));
    }

//API 7

    @DeleteMapping("/vendor/{vendorId}")
    public ResponseEntity<ResponseDet> deleteProductsByVendor(
            @PathVariable String vendorId
    ) {
        return ResponseEntity.ok(productService.deleteProductsByVendorId(vendorId));
    }

}
