package com.tekpyramid.kira_product_inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockCheckResult {
    private String productId;
    private Integer requested;
    private Integer available;
    private Boolean isAvailable;
}