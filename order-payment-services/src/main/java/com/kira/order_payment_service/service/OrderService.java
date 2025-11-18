package com.kira.order_payment_service.service;

import com.kira.order_payment_service.client.CartClient;
import com.kira.order_payment_service.client.InventoryClient;
import com.kira.order_payment_service.dto.OrderRequest;
import com.kira.order_payment_service.dto.OrderResponse;
import com.kira.order_payment_service.dto.inventory.StockCheckItem;
import com.kira.order_payment_service.dto.inventory.StockCheckResult;
import com.kira.order_payment_service.entity.Order;
import com.kira.order_payment_service.entity.OrderItem;
import com.kira.order_payment_service.exception.OutOfStockException;
import com.kira.order_payment_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartClient cartClient;
    private final InventoryClient inventoryClient;

    // ------------------ 1. Place direct order ------------------
    public OrderResponse placeOrder(OrderRequest request) {

        List<StockCheckItem> stockItems = request.getItems().stream()
                .map(i -> new StockCheckItem(i.getProductId(), i.getQuantity()))
                .collect(Collectors.toList());

        List<StockCheckResult> results = inventoryClient.checkStock(stockItems);

        for (StockCheckResult result : results) {
            if (!result.isAvailable()) {
                throw new OutOfStockException(
                        "Product " + result.getProductId() +
                                " requested: " + result.getRequested() +
                                " available: " + result.getAvailable()
                );
            }
        }

        double total = request.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        Order order = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .customerId(request.getCustomerId())
                .items(request.getItems())
                .totalAmount(total)
                .status("PENDING")
                .build();

        orderRepository.save(order);

        return convert(order);
    }

    // ------------------ 2. Place order from cart ------------------
    public OrderResponse placeOrderFromCart(String customerId) {
        var cart = cartClient.getCartByCustomer(customerId);
        var list = (List<?>) cart.get("items");

        List<OrderItem> items = list.stream().map(o -> {
            Map<String, Object> m = (Map<String, Object>) o;
            return new OrderItem(
                    UUID.randomUUID().toString(),
                    null,
                    m.get("productId").toString(),
                    ((Number) m.get("quantity")).intValue(),
                    ((Number) m.get("price")).doubleValue()
            );
        }).collect(Collectors.toList());

        OrderResponse response = placeOrder(new OrderRequest(customerId, items));

        cartClient.clearCart(customerId);

        return response;
    }

    // ------------------ 3. Update order with paymentId ------------------
    public void linkPaymentToOrder(String orderId, String paymentId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setPaymentId(paymentId);
            order.setStatus("CONFIRMED");
            orderRepository.save(order);
        });
    }

    // ------------------ 4. Get Order ------------------
    public OrderResponse getOrder(String id) {
        return orderRepository.findById(id).map(this::convert).orElse(null);
    }

    // ------------------ 5. Cancel Order ------------------
    public OrderResponse cancelOrder(String id) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus("CANCELLED");
                    orderRepository.save(order);
                    return convert(order);
                })
                .orElse(null);
    }

    private OrderResponse convert(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getOrderDate(),
                order.getStatus(),
                order.getPaymentId(),
                order.getItems()
        );
    }
}
