package com.kira.order_payment_service.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    private String orderItemId;
    private String orderId;
    private String productId;
    private Integer quantity;
    private Double price;
}
