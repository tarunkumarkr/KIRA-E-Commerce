package com.kira.userservice.controller;

import com.kira.userservice.dto.CustomerRequestDTO;
import com.kira.userservice.dto.CustomerResponseDTO;
import com.kira.userservice.response.ApiResponse;
import com.kira.userservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // 🔹 1️⃣ Register a new customer
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerCustomer(@Valid @RequestBody CustomerRequestDTO request) {
        String id = customerService.registerCustomer(request);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("Customer registered successfully")
                .data(id)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 🔹 2️⃣ Get customer by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDTO>> getCustomerById(@PathVariable String id) {
        CustomerResponseDTO customer = customerService.getCustomerById(id);

        ApiResponse<CustomerResponseDTO> response = ApiResponse.<CustomerResponseDTO>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("Customer fetched successfully")
                .data(customer)
                .build();

        return ResponseEntity.ok(response);
    }

    // 🔹 3️⃣ Get all customers
    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponseDTO>>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();

        ApiResponse<List<CustomerResponseDTO>> response = ApiResponse.<List<CustomerResponseDTO>>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("All customers fetched successfully")
                .data(customers)
                .build();

        return ResponseEntity.ok(response);
    }

    // 🔹 4️⃣ Update customer
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateCustomer(
            @PathVariable String id,
            @Valid @RequestBody CustomerRequestDTO request) {

        customerService.updateCustomer(id, request);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("Customer updated successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    // 🔹 5️⃣ Delete customer
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("Customer deleted successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}
