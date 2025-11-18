package com.tekpyramid.kira_product_inventory.dto;

// CategoryDto
import jakarta.validation.constraints.NotNull;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto extends BaseEntityDto{
    private String categoryId;

    @NotBlank(message = "name is required")
    @Size(max = 200, message = "name max 200 chars")
    private String name;

    @Size(max = 1000, message = "description max 1000 chars")
    private String description;

//    @NotNull(message = "productIds is required")
//    @Size(min = 1, message = "at least one category is required")
//    private List<@NotBlank(message = "productId cannot be blank") String> productIds;
    private List<String> productIds;

}
