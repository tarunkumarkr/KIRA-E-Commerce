package com.tekpyramid.kira_product_inventory.entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document("products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntity {

    @Id
    private String productId;

    @Field("vendor_id")
    private String vendorId;

    @Field("vendor_status")
    private boolean vendorSatatus;

    @Field("category_id")
    private String categoryId;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    @Field("price")
    private double price;

//    @Field("image")
//    private String image; // List of image URLs




}
