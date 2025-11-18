package com.kira.userservice.service;

import com.kira.userservice.entity.Vendor;
import com.kira.userservice.exception.ResourceNotFoundException;
import com.kira.userservice.repository.VendorRepository;
import com.kira.userservice.dto.VendorRequestDTO;
import com.kira.userservice.dto.VendorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String registerVendor(VendorRequestDTO request) {

        if (vendorRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        if (vendorRepository.existsByGstNumber(request.getGstNumber())) {
            throw new IllegalArgumentException("GST number already exists");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        Vendor vendor = Vendor.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .businessName(request.getBusinessName())
                 .gstNumber(request.getGstNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("VENDOR")
                .active(true)
                .build();

        Vendor saved = vendorRepository.save(vendor);
        return saved.getId();
    }

    @Override
    public VendorResponseDTO getVendorById(String id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + id));

        return mapToResponseDTO(vendor);
    }

    @Override
    public List<VendorResponseDTO> getAllVendors() {
        return vendorRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public void updateVendor(String id, VendorRequestDTO request) {
        Vendor existing = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + id));

        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setEmail(request.getEmail());
        existing.setPhoneNumber(request.getPhoneNumber());
        existing.setBusinessName(request.getBusinessName());
        existing.setGstNumber(request.getGstNumber());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords do not match");
            }
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        vendorRepository.save(existing);
    }

    @Override
    public void deleteVendor(String id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + id));

        vendorRepository.delete(vendor);
    }

    @Override
    public void updateVendorStatus(String id, boolean active) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + id));

        vendor.setActive(active);
        vendorRepository.save(vendor);
    }

    private VendorResponseDTO mapToResponseDTO(Vendor vendor) {
        return VendorResponseDTO.builder()
                .firstName(vendor.getFirstName())
                .lastName(vendor.getLastName())
                .email(vendor.getEmail())
                .phoneNumber(vendor.getPhoneNumber())
                .businessName(vendor.getBusinessName())
                .gstNumber(vendor.getGstNumber())
                .build();
    }
}
