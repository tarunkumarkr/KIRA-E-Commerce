package com.kira.order_payment_service.config;


import com.kira.order_payment_service.dto.OrderResponse;
import com.kira.order_payment_service.entity.Order;
import org.springframework.stereotype.Component;


@Component
public class MapperUtil {
    public OrderResponse toOrderResponse(Order o) {
        if (o == null) return null;
        return OrderResponse.builder()
                .orderId(o.getOrderId())
                .customerId(o.getCustomerId())
                .totalAmount(o.getTotalAmount())
                .orderDate(o.getOrderDate())
                .status(o.getStatus())
                .paymentId(o.getPaymentId())
                .items(o.getItems())
                .build();
    }
}