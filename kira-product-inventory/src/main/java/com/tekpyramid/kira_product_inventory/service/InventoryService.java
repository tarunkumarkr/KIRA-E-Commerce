package com.tekpyramid.kira_product_inventory.service;

import com.tekpyramid.kira_product_inventory.dto.ReduceStockRequest;
import com.tekpyramid.kira_product_inventory.dto.ResponseDet;
import com.tekpyramid.kira_product_inventory.dto.StockCheckItem;
import com.tekpyramid.kira_product_inventory.entity.Inventory;

import java.util.List;
import java.util.Map;

public interface InventoryService {
    ResponseDet checkStock(List<StockCheckItem> items);
    ResponseDet reduceStock(ReduceStockRequest request);
}