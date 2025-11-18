package com.tekpyramid.kira.Authservice.client;

import com.tekpyramid.kira.Authservice.dto.VendorRequestDTO;
import com.tekpyramid.kira.Authservice.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "USER-SERVICE",
        contextId = "vendorClient",
        path = "/api/v1/vendors",
        configuration = com.tekpyramid.kira.Authservice.configuration.FeignAuthForwardConfig.class
)
public interface VendorServiceClient {

    // 1️⃣ Register Vendor
    @PostMapping("/register")
    ApiResponse<?> registerVendor(@RequestBody VendorRequestDTO request);

    // 2️⃣ Get Vendor by ID
    @GetMapping("/{id}")
    ApiResponse<?> getVendorById(@PathVariable("id") String id);

    // 3️⃣ Get all Vendors
    @GetMapping("/all")
    ApiResponse<?> getAllVendors();

    // 4️⃣ Update Vendor
    @PutMapping("/{id}")
    ApiResponse<?> updateVendor(
            @PathVariable("id") String id,
            @RequestBody VendorRequestDTO request
    );

    // 5️⃣ Delete Vendor
    @DeleteMapping("/{id}")
    ApiResponse<?> deleteVendor(@PathVariable("id") String id);
}
