package com.kira.order_payment_service.service;

import com.kira.order_payment_service.dto.PaymentRequest;
import com.kira.order_payment_service.dto.PaymentResponse;
import com.kira.order_payment_service.entity.Payment;
import com.kira.order_payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    public PaymentResponse makePayment(PaymentRequest request) {

        boolean success = request.getAmount() != null && request.getAmount() > 0;

        Payment payment = Payment.builder()
                .paymentId(UUID.randomUUID().toString())
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(success ? "SUCCESS" : "FAILED")
                .transactionId(UUID.randomUUID().toString())
                .build();

        paymentRepository.save(payment);

        if (success) {
            orderService.linkPaymentToOrder(request.getOrderId(), payment.getPaymentId());
        }

        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .paymentStatus(payment.getPaymentStatus())
                .transactionId(payment.getTransactionId())
                .httpStatus(success ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .message(success ? "Payment Successful" : "Payment Failed")
                .build();
    }

    public PaymentResponse getPayment(String id) {
        return paymentRepository.findById(id)
                .map(p -> PaymentResponse.builder()
                        .paymentId(p.getPaymentId())
                        .orderId(p.getOrderId())
                        .paymentStatus(p.getPaymentStatus())
                        .transactionId(p.getTransactionId())
                        .httpStatus(HttpStatus.OK)
                        .message("Payment Found")
                        .build())
                .orElse(null);
    }
}
