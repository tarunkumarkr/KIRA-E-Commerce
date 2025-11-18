package com.kira.order_payment_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "cart-service", url = "http://localhost:8085")
public interface CartClient {

    @GetMapping("/api/cart/{customerId}")
    Map<String, Object> getCartByCustomer(@PathVariable("customerId") String customerId);

    @DeleteMapping("/api/cart/{customerId}/clear")
    void clearCart(@PathVariable("customerId") String customerId);
}
