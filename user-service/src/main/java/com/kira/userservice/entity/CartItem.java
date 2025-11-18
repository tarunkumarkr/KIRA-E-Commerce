package com.kira.userservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "cart_items")
public class CartItem extends BaseEntity {

    @Id
    private String id;

    @DBRef(lazy = true)
    private Cart cart;

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "Product Name is required")
    private String productName;

    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be > 0")
    private Double unitPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Minimum quantity is 1")
    private Integer quantity;

    @NotNull(message = "Total price is required")
    @Positive(message = "Total price must be > 0")
    private Double totalPrice;

}
