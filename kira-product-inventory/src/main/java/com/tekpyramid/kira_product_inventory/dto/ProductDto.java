package com.tekpyramid.kira_product_inventory.dto;

// package com.tekpyramid.kira_product_inventory.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto extends BaseEntityDto{
    private String productId; // optional for create; service will set id

//    @NotBlank(message = "vendorId is required")
    private String vendorId;

    private boolean vendorSatatus;


    @NotBlank(message = "name is required")
    @Size(max = 200, message = "name must be <= 200 chars")
    private String name;

    @Size(max = 2000, message = "description must be <= 2000 chars")
    private String description;

    @NotNull(message = "categoryId is required")
    private String categoryId;

    @NotNull(message = "price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "price must be greater than 0")
    private Double price;

//    @DecimalMin(value = "0.0", inclusive = true, message = "discountedPrice must be >= 0")
//    private Double discountedPrice;
//
//    @Size(max = 100, message = "sku max 100 chars")
//    private String sku;
//
//    @Size(max = 100, message = "brand max 100 chars")
//    private String brand;
//
//    private String image;

    @NotNull(message = "quantity is required")
    @Min(value = 0, message = "quantity must be >= 0")
    private Integer quantity;
//    private Boolean isActive;


}

