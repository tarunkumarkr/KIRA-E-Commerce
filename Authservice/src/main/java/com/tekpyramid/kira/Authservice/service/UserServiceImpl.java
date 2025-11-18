package com.tekpyramid.kira.Authservice.service;

import com.tekpyramid.kira.Authservice.client.CustomerServiceClient;
import com.tekpyramid.kira.Authservice.client.VendorServiceClient;
import com.tekpyramid.kira.Authservice.dto.CustomerRequestDTO;
import com.tekpyramid.kira.Authservice.dto.LoginRequest;
import com.tekpyramid.kira.Authservice.dto.VendorRequestDTO;
import com.tekpyramid.kira.Authservice.entity.Auth;
import com.tekpyramid.kira.Authservice.entity.Role;
import com.tekpyramid.kira.Authservice.repository.AuthRepository;
import com.tekpyramid.kira.Authservice.response.ApiResponse;
import com.tekpyramid.kira.Authservice.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerServiceClient customerServiceClient;
    private final VendorServiceClient vendorServiceClient;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;


    @Override
    public ApiResponse<Auth> registerCustomer(CustomerRequestDTO request) {

        // 1️⃣ Validate password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ApiResponse.<Auth>builder().message("Password and Confirm Password do not match!").build();
        }

        // 2️⃣ Check duplicate email
        if (authRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.<Auth>builder().message("Email already registered!").build();
        }

        // 3️⃣ Generate username
        String mergedUserName = (request.getFirstName() + request.getLastName()).trim().toLowerCase();

        // Prevent username duplication
        if (authRepository.existsByUserName(mergedUserName)) {
            mergedUserName = mergedUserName + System.currentTimeMillis();
        }

        // 4️⃣ Encrypt password
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 5️⃣ Prepare Auth object
        Auth auth = Auth.builder().userName(mergedUserName).email(request.getEmail()).password(encodedPassword).role(Role.CUSTOMER).active(true).build();

        // 6️⃣ Save Auth user
        Auth savedAuth = authRepository.save(auth);

        // 7️⃣ Call USER-SERVICE using Feign
        try {
            customerServiceClient.registerCustomer(request);
        } catch (Exception ex) {

            // Rollback to avoid inconsistent DB state
            authRepository.delete(savedAuth);

            return ApiResponse.<Auth>builder().message("Customer-Service unavailable. Rolled back Auth entry!").build();
        }

        // 8️⃣ Return success response
        return ApiResponse.<Auth>builder().message("Customer registered successfully!").data(savedAuth)   // your ApiResponse supports data
                .build();
    }


    @Override
    public ApiResponse<Auth> registerVendor(VendorRequestDTO request) {

        // 1️⃣ Validate Password Match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ApiResponse.<Auth>builder()
                    .message("Password and Confirm Password do not match")
                    .build();
        }

        // 2️⃣ Check Email Already Exists in Auth DB
        if (authRepository.findByEmail(request.getEmail()).isPresent()) {
            return ApiResponse.<Auth>builder()
                    .message("Email already registered! Please login.")
                    .build();
        }

        // 3️⃣ Encode Password
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 4️⃣ Generate Username
        String mergedUserName = (request.getFirstName() + request.getLastName())
                .trim().toLowerCase();

        if (authRepository.existsByUserName(mergedUserName)) {
            mergedUserName += System.currentTimeMillis();
        }

        // 5️⃣ Save vendor in Auth DB
        Auth savedVendor = authRepository.save(
                Auth.builder()
                        .userName(mergedUserName)
                        .email(request.getEmail())
                        .password(encodedPassword)
                        .role(Role.VENDOR)
                        .active(true)
                        .build()
        );



        // 7️⃣ Call vendor-service
        try {
            vendorServiceClient.registerVendor(request);
        } catch (Exception ex) {

            // ❗ Rollback AUTH entry if vendor-service fails
            authRepository.delete(savedVendor);

            return ApiResponse.<Auth>builder()
                    .message("Vendor-Service unavailable. Rolled back Auth entry!")
                    .build();
        }

        // 8️⃣ Success response
        return ApiResponse.<Auth>builder()
                .message("Vendor registered successfully!")
                .data(savedVendor)
                .build();
    }


    @Override
    public ApiResponse<String> login(LoginRequest request) {

        String input = request.getUserName().trim().toLowerCase();
        boolean isEmail = input.contains("@");

        Auth user;

        if (isEmail) {
            user = authRepository.findByEmail(input).orElse(null);
        } else {
            String normalized = input.replaceAll("\\s+", "");
            user = authRepository.findByUserName(normalized).orElse(null);
        }

        // User not found
        if (user == null) {
            return ApiResponse.<String>builder()
                    .message("Invalid username/email or password")
                    .build();
        }

        // Wrong password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.<String>builder()
                    .message("Invalid username/email or password")
                    .build();
        }

        // Load user details for Spring Security
        var userDetails = userDetailsService.loadUserByUsername(user.getUserName());

        // Generate access token with ROLE + ID
        String accessToken = jwtUtils.generateToken(
                userDetails,
                user.getRole().name(),   // FIXED
                user.getId()             // FIXED
        );

        // Generate refresh token
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        // Extract expirations
        Date accessExp = jwtUtils.extractExpiration(accessToken);
        Date refreshExp = jwtUtils.extractExpiration(refreshToken);

        LocalDateTime tokenExpiry =
                accessExp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime refreshTokenExpiry =
                refreshExp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        return ApiResponse.<String>builder()
                .message("Login successful")
                .accessToken(accessToken)
                .tokenExpiry(tokenExpiry)
                .refreshToken(refreshToken)
                .refreshTokenExpiry(refreshTokenExpiry)
                .build();
    }



    private void sendCustomerToCustomerService(CustomerRequestDTO request) {
        try {
            customerServiceClient.registerCustomer(request);
            System.out.println("✔ Sent customer to customer-service via Feign");
        } catch (Exception e) {
            System.err.println("❌ Failed to send customer: " + e.getMessage());
        }
    }


    private void sendVendorToVendorService(VendorRequestDTO request) {
        try {
            vendorServiceClient.registerVendor(request);
            System.out.println("✔ Sent vendor to vendor-service");
        } catch (Exception e) {
            System.err.println("❌ Failed to send vendor to vendor-service: " + e.getMessage());
        }
    }

}

