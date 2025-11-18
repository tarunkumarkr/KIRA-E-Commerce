package com.tekpyramid.kira_product_inventory.exception;

import com.tekpyramid.kira_product_inventory.dto.ResponseDet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDet> handleResourceNotFound(ResourceNotFoundException ex) {
        ResponseDet response = new ResponseDet(ex.getMessage(), true, HttpStatus.NOT_FOUND, null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ResponseDet> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        ResponseDet response = new ResponseDet(ex.getMessage(), true, HttpStatus.FORBIDDEN, null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ResponseDet> handleInsufficientStock(InsufficientStockException ex) {
        ResponseDet response = new ResponseDet(ex.getMessage(), true, HttpStatus.BAD_REQUEST, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDet> handleGenericException(Exception ex) {
        ResponseDet response = new ResponseDet("An error occurred: " + ex.getMessage(), true, HttpStatus.INTERNAL_SERVER_ERROR, null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }




    @ExceptionHandler(VendorCurrentlyInactive.class)
    public ResponseEntity<ResponseDet> handleVendorCurrentlyInactive(Exception ex) {
        ResponseDet response = new ResponseDet(ex.getMessage(), true, HttpStatus.FORBIDDEN, null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDet> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());

        ResponseDet response = new ResponseDet("Validation failed", true, HttpStatus.BAD_REQUEST, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}