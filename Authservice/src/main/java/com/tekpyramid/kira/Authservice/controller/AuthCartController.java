package com.tekpyramid.kira.Authservice.controller;

import com.tekpyramid.kira.Authservice.client.CartServiceClient;
import com.tekpyramid.kira.Authservice.dto.CartItemRequestDTO;
import com.tekpyramid.kira.Authservice.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class AuthCartController {

    private final CartServiceClient cartClient;

    // -------------------------------
    // GET CART
    // -------------------------------
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/{customerId}")
    public ApiResponse<?> getCart(@PathVariable String customerId) {
        return cartClient.getCart(customerId);
    }

    // -------------------------------
    // ADD ITEM
    // -------------------------------
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{customerId}/items")
    public ApiResponse<?> addItem(
            @PathVariable String customerId,
            @RequestBody CartItemRequestDTO request
    ) {
        return cartClient.addItem(customerId, request);
    }

    // -------------------------------
    // UPDATE ITEM
    // -------------------------------
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{customerId}/items/{itemId}")
    public ApiResponse<?> updateItem(
            @PathVariable String customerId,
            @PathVariable String itemId,
            @RequestBody CartItemRequestDTO request
    ) {
        return cartClient.updateItem(customerId, itemId, request);
    }

    // -------------------------------
    // DELETE ITEM
    // -------------------------------
    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/{customerId}/items/{itemId}")
    public ApiResponse<?> deleteItem(
            @PathVariable String customerId,
            @PathVariable String itemId
    ) {
        return cartClient.deleteItem(customerId, itemId);
    }
}
