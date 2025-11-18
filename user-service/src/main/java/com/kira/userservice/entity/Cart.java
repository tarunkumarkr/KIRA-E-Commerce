package com.kira.userservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "carts")
public class Cart extends BaseEntity {

    @Id
    private String id;

    @JsonIgnore
    @DBRef(lazy = true)
    private Customer customer;

    @JsonIgnoreProperties("cart")
    @DBRef(lazy = true)
    private List<CartItem> items = new ArrayList<>();

    private Double totalAmount = 0.0;
}
