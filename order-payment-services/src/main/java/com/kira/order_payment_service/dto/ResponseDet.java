package com.kira.order_payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDet {
    private String message;
    private boolean error;
    private HttpStatus httpStatus;
    private Object data;
}
