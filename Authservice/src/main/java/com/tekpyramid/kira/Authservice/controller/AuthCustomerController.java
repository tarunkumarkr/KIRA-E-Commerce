package com.tekpyramid.kira.Authservice.controller;

import com.tekpyramid.kira.Authservice.client.CustomerServiceClient;
import com.tekpyramid.kira.Authservice.client.VendorServiceClient;
import com.tekpyramid.kira.Authservice.dto.CustomerRequestDTO;
import com.tekpyramid.kira.Authservice.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/customers")
@RequiredArgsConstructor
public class AuthCustomerController {

    private final CustomerServiceClient customerServiceClient;
    private final VendorServiceClient vendorServiceClient;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ApiResponse<?> getCustomer(@PathVariable String id) {
        return customerServiceClient.getCustomerById(id);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("")
    public ApiResponse<?> fetchAllCustomers() {
        return customerServiceClient.getAllCustomers();
    }

    @PreAuthorize("hasAuthority('CUSTOMER')")
    @PutMapping("/{id}")
    public ApiResponse<?> updateCustomer(
            @PathVariable("id") String id,
            @RequestBody CustomerRequestDTO request
    ) {
        return customerServiceClient.updateCustomer(id, request);
    }

    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('CUSTOMER') and #id == principal.id)")
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteCustomer(@PathVariable String id) {
        return customerServiceClient.deleteCustomer(id);
    }




}

