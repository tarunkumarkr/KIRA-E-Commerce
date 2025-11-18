package com.kira.userservice.service;

import com.kira.userservice.entity.Cart;
import com.kira.userservice.entity.CartItem;
import com.kira.userservice.entity.Customer;
import com.kira.userservice.exception.QuantityException;
import com.kira.userservice.exception.ResourceNotFoundException;
import com.kira.userservice.repository.CartItemRepository;
import com.kira.userservice.repository.CartRepository;
import com.kira.userservice.repository.CustomerRepository;
import com.kira.userservice.dto.CartItemRequestDTO;
import com.kira.userservice.dto.CartItemResponseDTO;
import com.kira.userservice.dto.CartResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;


    @Override
    public CartResponseDTO getCartByCustomerId(String customerId) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        // Clean null values to prevent NPE
        cart.getItems().removeIf(Objects::isNull);

        return mapToResponse(cart);
    }


    @Override
    public CartResponseDTO addItem(String customerId, CartItemRequestDTO request) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().removeIf(Objects::isNull);

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.setUnitPrice(request.getUnitPrice());
            item.setProductName(request.getProductName());   // ✅ FIX
            item.setTotalPrice(item.getQuantity() * item.getUnitPrice());
            cartItemRepository.save(item);

        } else {

            CartItem item = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .productName(request.getProductName())   // ✔ correct
                    .unitPrice(request.getUnitPrice())
                    .quantity(request.getQuantity())
                    .totalPrice(request.getUnitPrice() * request.getQuantity())
                    .build();

            CartItem saved = cartItemRepository.save(item);
            cart.getItems().add(saved);
        }

        updateTotal(cart);
        return mapToResponse(cart);
    }



    @Override
    public CartResponseDTO updateItem(String customerId, String itemId, CartItemRequestDTO request) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found"));

        if (request.getQuantity() == 0) {
            throw new QuantityException("Quantity cannot be zero. Please add at least 1 item.");
        }


        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());
        item.setProductName(request.getProductName());
        item.setTotalPrice(request.getQuantity() * request.getUnitPrice());

        cartItemRepository.save(item);
        updateTotal(cart);

        return mapToResponse(cart);
    }



    @Override
    public CartResponseDTO deleteItem(String customerId, String itemId) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        // delete from DB
        cartItemRepository.delete(item);

        // safe removal
        cart.getItems().removeIf(i -> i == null || itemId.equals(i.getId()));

        updateTotal(cart);

        return mapToResponse(cart);
    }



    private void updateTotal(Cart cart) {

        cart.getItems().removeIf(Objects::isNull);

        double total = cart.getItems().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        cart.setTotalAmount(total);
        cartRepository.save(cart);
    }



    private CartResponseDTO mapToResponse(Cart cart) {

        List<CartItemResponseDTO> items = cart.getItems() == null
                ? List.of()
                : cart.getItems().stream()
                .filter(Objects::nonNull)
                .map(i -> CartItemResponseDTO.builder()
                        .productId(i.getProductId())
                        .quantity(i.getQuantity())
                        .productName(i.getProductName())
                        .unitPrice(i.getUnitPrice())
                        .build())
                .toList();

        return CartResponseDTO.builder()
                .id(cart.getId())
                .totalAmount(cart.getTotalAmount())
                .items(items)
                .build();
    }


    public Cart createNewCartForCustomer(Customer customer) {

        Cart cart = Cart.builder()
                .customer(customer)
                .items(new ArrayList<>())
                .totalAmount(0.0)
                .build();

        return cartRepository.save(cart);
    }
}
