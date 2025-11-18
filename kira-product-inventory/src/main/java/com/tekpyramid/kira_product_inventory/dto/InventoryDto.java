package com.tekpyramid.kira_product_inventory.dto;
// InventoryDto
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryDto extends BaseEntityDto{
    private String inventoryId;

    @NotBlank(message = "productId is required")
    private String productId;

    @NotNull(message = "quantity is required")
    @Min(value = 0, message = "quantity must be >= 0")
    private Integer quantity;

//    @Min(value = 0, message = "reservedQuantity must be >= 0")
//    private Integer reservedQuantity = 0;
//
//    @Min(value = 0, message = "minStockLevel must be >= 0")
//    private Integer minStockLevel = 10;
//
//    @Min(value = 0, message = "maxStockLevel must be >= 0")
//    private Integer maxStockLevel = 1000;
}
