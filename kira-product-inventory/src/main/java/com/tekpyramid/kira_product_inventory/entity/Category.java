package com.tekpyramid.kira_product_inventory.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document("categories")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Category extends BaseEntity {

    @Id
    private String categoryId;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("product_ids")
    private List<String> productIds; // References to products in this category
}
