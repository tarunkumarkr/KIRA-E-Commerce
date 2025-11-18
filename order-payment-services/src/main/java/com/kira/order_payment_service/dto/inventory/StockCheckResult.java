package com.kira.order_payment_service.dto.inventory;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockCheckResult {
    private String productId;
    private Integer requested;
    private Integer available;
    private boolean availableStatus;


    public boolean isAvailable() {
        return availableStatus;
    }
}
