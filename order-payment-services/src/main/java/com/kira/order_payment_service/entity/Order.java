package com.kira.order_payment_service.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "orders")
public class Order {
    @Id
    private String orderId;

    private String customerId;
    private Double totalAmount;
    private LocalDateTime orderDate = LocalDateTime.now();
    private String status; // PENDING, CONFIRMED, CANCELLED
    private String paymentId; // link to Payment

    private List<OrderItem> items;
}
