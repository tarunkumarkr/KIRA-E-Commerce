package com.kira.userservice.repository;

import com.kira.userservice.entity.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CartItemRepository extends MongoRepository<CartItem, String> {

    List<CartItem> findAllByCartId(String cartId);


    void deleteAllByCart_Id(String cartId);
}

