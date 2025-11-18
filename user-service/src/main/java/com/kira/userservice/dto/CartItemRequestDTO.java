package com.kira.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequestDTO {

    @NotBlank(message = "productId is required")
    private String productId;

    @NotBlank(message = "Product Name is required")
    private String productName;

    @NotNull(message = "unitPrice is required")
    @Positive(message = "unitPrice must be > 0")
    private Double unitPrice;

    @NotNull(message = "quantity is required")
    @Min(value = 1, message = "quantity must be >= 1")
    private Integer quantity;

}
