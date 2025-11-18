package com.kira.order_payment_service.controller;

import com.kira.order_payment_service.dto.PaymentRequest;
import com.kira.order_payment_service.dto.PaymentResponse;
import com.kira.order_payment_service.dto.ResponseDet;
import com.kira.order_payment_service.service.PaymentService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ResponseDet> makePayment(@Validated @RequestBody PaymentRequest request) {

        PaymentResponse paymentResponse = paymentService.makePayment(request);

        ResponseDet response = new ResponseDet(
                paymentResponse.getMessage(),
                paymentResponse.getHttpStatus().isError(),
                paymentResponse.getHttpStatus(),
                paymentResponse
        );

        return ResponseEntity.status(paymentResponse.getHttpStatus()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDet> getPayment(@PathVariable String id) {

        PaymentResponse responseObj = paymentService.getPayment(id);

        if (responseObj == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDet("Payment Not Found", true, HttpStatus.NOT_FOUND, null));
        }

        ResponseDet response = new ResponseDet(
                "Payment Found",
                false,
                HttpStatus.OK,
                responseObj
        );

        return ResponseEntity.ok(response);
    }
}
