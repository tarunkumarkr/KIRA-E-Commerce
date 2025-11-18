package com.kira.userservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductFeignResponseDTO {
    private String id;
    private String name;
    private Double price;

}
