package com.tekpyramid.kira_product_inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReduceStockRequest {
    private List<ReduceStockItem> items;
}
