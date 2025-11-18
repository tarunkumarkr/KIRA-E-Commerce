package com.tekpyramid.kira.Authservice.controller;

import com.tekpyramid.kira.Authservice.client.CustomerServiceClient;
import com.tekpyramid.kira.Authservice.client.VendorServiceClient;
import com.tekpyramid.kira.Authservice.dto.CustomerRequestDTO;
import com.tekpyramid.kira.Authservice.dto.VendorRequestDTO;
import com.tekpyramid.kira.Authservice.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/vendors")
@RequiredArgsConstructor
public class AuthVendorController {

    private final CustomerServiceClient customerServiceClient;
    private final VendorServiceClient vendorServiceClient;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ApiResponse<?> fetchVendor(@PathVariable("id") String id) {
        return vendorServiceClient.getVendorById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ApiResponse<?> getAllVendors() {
        return vendorServiceClient.getAllVendors();
    }

    @PreAuthorize("hasRole('VENDOR')")
    @PutMapping("/{id}")
    public ApiResponse<?> updateVendor(
            @PathVariable("id") String id,
            @RequestBody VendorRequestDTO request
    ) {
        return vendorServiceClient.updateVendor(id, request);
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('VENDOR') and #id == principal.id)")
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteVendor(@PathVariable("id") String id) {
        return vendorServiceClient.deleteVendor(id);
    }

}


