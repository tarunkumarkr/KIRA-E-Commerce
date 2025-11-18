package com.kira.order_payment_service.repository;

import com.kira.order_payment_service.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {}
