package com.tekpyramid.kira_product_inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReduceStockResult {
    private String productId;
    private Integer requested;
    private Integer remaining;   // remaining after decrement (or current available)
    private boolean reduced;     // true if decremented
    private String message;      // optional reason on failure
}