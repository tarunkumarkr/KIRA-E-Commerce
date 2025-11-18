package com.tekpyramid.kira_product_inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockCheckItem {
    private String productId;
    private Integer quantity;
}