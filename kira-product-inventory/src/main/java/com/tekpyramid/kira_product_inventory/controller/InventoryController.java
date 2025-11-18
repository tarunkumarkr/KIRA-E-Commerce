package com.tekpyramid.kira_product_inventory.controller;

import com.tekpyramid.kira_product_inventory.dto.InventoryDto;
import com.tekpyramid.kira_product_inventory.dto.ReduceStockRequest;
import com.tekpyramid.kira_product_inventory.dto.ResponseDet;
import com.tekpyramid.kira_product_inventory.dto.StockCheckItem;
import com.tekpyramid.kira_product_inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/check-stock")
    public ResponseEntity<ResponseDet> checkStock(@RequestBody List<StockCheckItem> items) {
        ResponseDet resp = inventoryService.checkStock(items);
        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
    }
    @PostMapping("/reduce")
    public ResponseEntity<ResponseDet> reduceStock(@RequestBody ReduceStockRequest request) {
        ResponseDet resp = inventoryService.reduceStock(request);
        return ResponseEntity.status(resp.getHttpStatus()).body(resp);
    }
}

