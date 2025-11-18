package com.kira.order_payment_service.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private String orderId;
    private String customerId;
    private Double totalAmount;
    private LocalDateTime orderDate;
    private String status;
    private String paymentId;
    private List<?> items;
}
