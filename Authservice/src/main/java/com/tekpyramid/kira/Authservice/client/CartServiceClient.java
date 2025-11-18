package com.tekpyramid.kira.Authservice.client;

import com.tekpyramid.kira.Authservice.dto.CartItemRequestDTO;
import com.tekpyramid.kira.Authservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "USER-SERVICE" ,
        contextId = "cartClient",
        path = "/api/v1/cart"  )
public interface CartServiceClient {

    // 1️⃣ GET CART
    @GetMapping("/{customerId}")
    ApiResponse<?> getCart(@PathVariable("customerId") String customerId);

    // 2️⃣ ADD ITEM
    @PostMapping("/{customerId}/items")
    ApiResponse<?> addItem(
            @PathVariable("customerId") String customerId,
            @RequestBody CartItemRequestDTO request
    );

    // 3️⃣ UPDATE ITEM
    @PutMapping("/{customerId}/items/{itemId}")
    ApiResponse<?> updateItem(
            @PathVariable("customerId") String customerId,
            @PathVariable("itemId") String itemId,
            @RequestBody CartItemRequestDTO request
    );

    // 4️⃣ DELETE ITEM
    @DeleteMapping("/{customerId}/items/{itemId}")
    ApiResponse<?> deleteItem(
            @PathVariable("customerId") String customerId,
            @PathVariable("itemId") String itemId
    );
}
