package com.kira.userservice.controller;

import com.kira.userservice.dto.CartItemRequestDTO;
import com.kira.userservice.dto.CartResponseDTO;
import com.kira.userservice.response.ApiResponse;
import com.kira.userservice.service.CartService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // GET CART
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> getCart(@PathVariable String customerId) {
        CartResponseDTO data = cartService.getCartByCustomerId(customerId);

        return ResponseEntity.ok(ApiResponse.<CartResponseDTO>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("Cart fetched successfully")
                .data(data)
                .build());
    }

    // ADD ITEM
    @PostMapping("/{customerId}/items")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addItem(
            @PathVariable String customerId,
            @RequestBody CartItemRequestDTO request) {

        CartResponseDTO data = cartService.addItem(customerId, request);

        return ResponseEntity.ok(ApiResponse.<CartResponseDTO>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("Product added to cart")
                .data(data)  // 👈 FIXED
                .build());
    }

    // UPDATE ITEM
    @PutMapping("/{customerId}/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> updateItem(
            @PathVariable String customerId,
            @PathVariable String itemId,
            @RequestBody CartItemRequestDTO request) {

        CartResponseDTO data = cartService.updateItem(customerId, itemId, request);

        return ResponseEntity.ok(ApiResponse.<CartResponseDTO>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("Cart Item updated")
                .data(data)
                .build());
    }

    // DELETE ITEM
    @DeleteMapping("/{customerId}/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> deleteItem(
            @PathVariable String customerId,
            @PathVariable String itemId) {

        CartResponseDTO data = cartService.deleteItem(customerId, itemId);

        return ResponseEntity.ok(
                ApiResponse.<CartResponseDTO>builder()
                        .timestamp(LocalDateTime.now())
                        .success(true)
                        .message("Item deleted")
                        .data(data)
                        .build()
        );
    }
}