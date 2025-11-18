package com.kira.userservice.controller;

import com.kira.userservice.dto.VendorRequestDTO;
import com.kira.userservice.dto.VendorResponseDTO;
import com.kira.userservice.response.ApiResponse;
import com.kira.userservice.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    // 🔹 1️⃣ Vendor Signup
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerVendor(@Valid @RequestBody VendorRequestDTO request) {
        String vendorId = vendorService.registerVendor(request);


        ApiResponse<String> response = ApiResponse.<String>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("Vendor registered successfully")
                .data("Vendor id"+" "+vendorId)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 🔹 2️⃣ Get Vendor by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorResponseDTO>> getVendorById(@PathVariable String id) {
        VendorResponseDTO vendor = vendorService.getVendorById(id);

        ApiResponse<VendorResponseDTO> response = ApiResponse.<VendorResponseDTO>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("Vendor fetched successfully")
                .data(vendor)
                .build();

        return ResponseEntity.ok(response);
    }

    // 🔹 3️⃣ Get All Vendors (Admin only)
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<VendorResponseDTO>>> getAllVendors() {
        List<VendorResponseDTO> vendors = vendorService.getAllVendors();

        ApiResponse<List<VendorResponseDTO>> response = ApiResponse.<List<VendorResponseDTO>>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("All vendors fetched successfully")
                .data(vendors)
                .build();

        return ResponseEntity.ok(response);
    }

    // 🔹 4️⃣ Update Vendor Details
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateVendor(
            @PathVariable String id,
            @Valid @RequestBody VendorRequestDTO request) {

        vendorService.updateVendor(id, request);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("Vendor updated successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    // 🔹 5️⃣ Delete Vendor
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVendor(@PathVariable String id) {
        vendorService.deleteVendor(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("Vendor deleted successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }

    // 🔹 6️⃣ Activate / Deactivate Vendor
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateVendorStatus(
            @PathVariable String id,
            @RequestParam boolean active) {

        vendorService.updateVendorStatus(id, active);

        String message = active
                ? "Vendor activated successfully"
                : "Vendor deactivated successfully";

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message(message)
                .data(null)
                .build();

        return ResponseEntity.ok(response);
    }
}
