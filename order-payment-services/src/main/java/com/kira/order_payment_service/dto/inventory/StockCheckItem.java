package com.kira.order_payment_service.dto.inventory;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockCheckItem {
    private String productId;
    private Integer quantity;
}
