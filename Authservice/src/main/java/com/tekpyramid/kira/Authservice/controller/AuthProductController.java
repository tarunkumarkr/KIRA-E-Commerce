package com.tekpyramid.kira.Authservice.controller;

import com.tekpyramid.kira.Authservice.client.ProductServiceClient;
import com.tekpyramid.kira.Authservice.dto.ProductRequestDto;
import com.tekpyramid.kira.Authservice.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/products")
@RequiredArgsConstructor
public class AuthProductController {
    private final ProductServiceClient productServiceClient;

    @PostMapping("/create")
    public ApiResponse<?> createProductFromAuth(@Valid @RequestBody ProductRequestDto dto, @RequestHeader("X-User-ID") String vendorId, @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        dto.setVendorId(vendorId);

        return productServiceClient.createProduct(dto, vendorId, userRole);
    }


}
