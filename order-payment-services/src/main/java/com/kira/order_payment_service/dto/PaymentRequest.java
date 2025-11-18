package com.kira.order_payment_service.dto;

import lombok.*;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    @NotNull
    private String orderId;
    @NotNull
    private Double amount;
    @NotNull
    private String paymentMethod;
}
