package com.kira.userservice.service;

import com.kira.userservice.dto.CartItemRequestDTO;
import com.kira.userservice.dto.CartResponseDTO;

public interface CartService {

   // CartResponseDTO createCart(String customerId);

    // ----------------------------------------------------------------
    // 1) GET CART (NO creation here)
    // ----------------------------------------------------------------
    CartResponseDTO getCartByCustomerId(String customerId);

    CartResponseDTO addItem(String cartId, CartItemRequestDTO request);

    CartResponseDTO updateItem(String cartId, String itemId, CartItemRequestDTO request);

    CartResponseDTO deleteItem(String cartId, String itemId);

   // CartResponseDTO getCart(String cartId);

}


