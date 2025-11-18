package com.kira.order_payment_service.controller;

import com.kira.order_payment_service.dto.OrderRequest;
import com.kira.order_payment_service.dto.OrderResponse;
import com.kira.order_payment_service.dto.ResponseDet;
import com.kira.order_payment_service.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseDet placeOrder(@Validated @RequestBody OrderRequest request) {
        OrderResponse resp = orderService.placeOrder(request);

        return new ResponseDet(
                "Order placed successfully",
                false,
                HttpStatus.CREATED,
                resp
        );
    }

    @PostMapping("/cart/{customerId}")
    public ResponseDet placeOrderFromCart(@PathVariable String customerId) {
        OrderResponse resp = orderService.placeOrderFromCart(customerId);

        return new ResponseDet(
                "Order placed from cart successfully",
                false,
                HttpStatus.CREATED,
                resp
        );
    }

    @GetMapping("/{id}")
    public ResponseDet getOrder(@PathVariable String id) {
        OrderResponse resp = orderService.getOrder(id);

        return resp == null
                ? new ResponseDet("Order not found", true, HttpStatus.NOT_FOUND, null)
                : new ResponseDet("Order fetched successfully", false, HttpStatus.OK, resp);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseDet cancelOrder(@PathVariable String id) {
        OrderResponse resp = orderService.cancelOrder(id);

        return resp == null
                ? new ResponseDet("Order not found", true, HttpStatus.NOT_FOUND, null)
                : new ResponseDet("Order cancelled successfully", false, HttpStatus.OK, resp);
    }
}
