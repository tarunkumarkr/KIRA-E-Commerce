package com.tekpyramid.kira.Authservice.service;

import com.tekpyramid.kira.Authservice.dto.CustomerRequestDTO;
import com.tekpyramid.kira.Authservice.dto.LoginRequest;
import com.tekpyramid.kira.Authservice.dto.VendorRequestDTO;
import com.tekpyramid.kira.Authservice.entity.Auth;
import com.tekpyramid.kira.Authservice.response.ApiResponse;
import jakarta.validation.Valid;

public interface UserService {



    ApiResponse<Auth> registerVendor(@Valid VendorRequestDTO request);


    ApiResponse<String> login(LoginRequest request);


    ApiResponse<Auth> registerCustomer(@Valid CustomerRequestDTO request);
}

