package com.kira.userservice.service;

import com.kira.userservice.dto.CustomerRequestDTO;
import com.kira.userservice.dto.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {
    String registerCustomer(CustomerRequestDTO request);
    CustomerResponseDTO getCustomerById(String id);
    List<CustomerResponseDTO> getAllCustomers();
    void updateCustomer(String id, CustomerRequestDTO request);
    void deleteCustomer(String id);
}
