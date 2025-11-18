package com.kira.userservice.service;

import com.kira.userservice.dto.VendorRequestDTO;
import com.kira.userservice.dto.VendorResponseDTO;

import java.util.List;

public interface VendorService {
    String registerVendor(VendorRequestDTO request);
    VendorResponseDTO getVendorById(String id);
    List<VendorResponseDTO> getAllVendors();
    void updateVendor(String id, VendorRequestDTO request);
    void deleteVendor(String id);
    void updateVendorStatus(String id, boolean active);
}
