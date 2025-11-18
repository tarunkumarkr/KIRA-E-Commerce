package com.kira.userservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderProductDTO {
    private String productId;
    private Integer quantity; // quantity ordered
}
