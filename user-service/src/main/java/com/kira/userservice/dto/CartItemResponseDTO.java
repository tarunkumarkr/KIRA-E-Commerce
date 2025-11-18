package com.kira.userservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponseDTO {
    private String productId;
    private String productName;
    private Double unitPrice;
    private Integer quantity;

}
