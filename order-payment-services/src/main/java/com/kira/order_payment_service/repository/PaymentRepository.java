package com.kira.order_payment_service.repository;

import com.kira.order_payment_service.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, String> {}
