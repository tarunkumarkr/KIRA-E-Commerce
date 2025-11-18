package com.tekpyramid.kira_product_inventory.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("inventory")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Inventory extends BaseEntity {

    @Id
    private String inventoryId;

    @Field("product_id")
    private String productId;

    @Field("quantity")
    private Integer quantity;

    @Field("vendor_id")
    private String vendorId;
//
//    @Field("reserved_quantity")
//    private Integer reservedQuantity = 0; // For orders in process
//
//    @Field("min_stock_level")
//    private Integer minStockLevel = 10;
//
//    @Field("max_stock_level")
//    private Integer maxStockLevel = 1000;
}
