package com.kira.order_payment_service.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "payments")
public class Payment {
    @Id
    private String paymentId;

    private String orderId;
    private String paymentStatus; // SUCCESS, FAILED, PENDING
    private String paymentMethod; // CARD, UPI, NETBANKING
    private Double amount;
    private String transactionId; // external txn id
    private LocalDateTime createdAt = LocalDateTime.now();
}
