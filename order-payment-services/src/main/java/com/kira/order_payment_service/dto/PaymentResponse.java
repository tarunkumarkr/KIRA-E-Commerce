package com.kira.order_payment_service.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private String paymentId;
    private String orderId;
    private String paymentStatus;
    private String transactionId;
    private HttpStatus httpStatus;
    private String message;
}
