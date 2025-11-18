package com.tekpyramid.kira.Authservice.client;

import com.tekpyramid.kira.Authservice.dto.CustomerRequestDTO;
import com.tekpyramid.kira.Authservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "USER-SERVICE",
        contextId = "customerClient",
        path = "/api/v1/customers",
        configuration = com.tekpyramid.kira.Authservice.configuration.FeignAuthForwardConfig.class
        // ⭐ Common base path
)
public interface CustomerServiceClient {

    // 1️⃣ Register customer
    @PostMapping("/register")
    ApiResponse<String> registerCustomer(@RequestBody CustomerRequestDTO request);

    // 2️⃣ Get customer by ID
    @GetMapping("/{id}")
    ApiResponse<?> getCustomerById(@PathVariable("id") String id);

    // 3️⃣ Get all customers
    @GetMapping("")
    ApiResponse<?> getAllCustomers();

    // 4️⃣ Update customer
    @PutMapping("/{id}")
    ApiResponse<?> updateCustomer(
            @PathVariable("id") String id,
            @RequestBody CustomerRequestDTO request
    );

    // 5️⃣ Delete customer
    @DeleteMapping("/{id}")
    ApiResponse<?> deleteCustomer(@PathVariable("id") String id);
}
