package com.kira.order_payment_service.dto;

import com.kira.order_payment_service.entity.OrderItem;
import lombok.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    @NotNull(message = "Customer ID cannot be null")
    private String customerId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItem> items;
}
