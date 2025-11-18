package com.kira.order_payment_service.client;

import com.kira.order_payment_service.dto.inventory.StockCheckItem;
import com.kira.order_payment_service.dto.inventory.StockCheckResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "inventory-service", url = "http://localhost:8083")
public interface InventoryClient {

    @PostMapping("/api/inventory/check-stock")
    List<StockCheckResult> checkStock(@RequestBody List<StockCheckItem> items);

    @PostMapping("/api/inventory/reduce-stock")
    void reduceStock(@RequestBody List<StockCheckItem> items);
}
