package com.tekpyramid.kira.Authservice.client;

import com.tekpyramid.kira.Authservice.dto.ProductRequestDto;
import com.tekpyramid.kira.Authservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(
        name = "PRODUCT-INVENTORY-SERVICE",
        path = "/api/v1/products"
)
public interface ProductServiceClient {

    // 1️⃣ Create Product
    @PostMapping("/create")
    ApiResponse<?> createProduct(@RequestBody ProductRequestDto dto,
                                 @RequestHeader("X-User-ID") String vendorId,
                                 @RequestHeader("X-User-Role") String userRole);



    // 2️⃣ Create Products in Bulk
    @PostMapping("/save-bulk")
    ApiResponse<?> createProductsBulk(
            @RequestBody Object productDtos,
            @RequestHeader("X-User-ID") String vendorId
    );

    // 3️⃣ Update Product
    @PutMapping("/{productId}")
    ApiResponse<?> updateProduct(
            @PathVariable("productId") String productId,
            @RequestBody Object productDto,
            @RequestHeader("X-User-ID") String vendorId
    );

    // 4️⃣ Get products by vendorId
    @GetMapping("/vendor/{vendorId}")
    ApiResponse<?> getProductsByVendorId(
            @PathVariable("vendorId") String vendorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    );

    // 5️⃣ Get all products
    @GetMapping
    ApiResponse<?> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    );

    // 6️⃣ Get product by productId
    @GetMapping("/{productId}")
    ApiResponse<?> getProductById(
            @PathVariable("productId") String productId
    );

    // 7️⃣ Get products by categoryId
    @GetMapping("/category/{categoryId}")
    ApiResponse<?> getProductsByCategoryId(
            @PathVariable("categoryId") String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    );

    // 8️⃣ Search products by keyword
    @GetMapping("/search")
    ApiResponse<?> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    );

    // 9️⃣ Delete product by productId
    @DeleteMapping("/{productId}")
    ApiResponse<?> deleteProductById(
            @PathVariable("productId") String productId
    );

    // 🔟 Delete products by vendorId
    @DeleteMapping("/vendor/{vendorId}")
    ApiResponse<?> deleteProductsByVendor(
            @PathVariable("vendorId") String vendorId
    );
}
