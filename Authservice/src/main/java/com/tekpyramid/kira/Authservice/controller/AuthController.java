package com.tekpyramid.kira.Authservice.controller;

//import com.tekpyramid.kira.Authservice.client.CustomerServiceClient;
//import com.tekpyramid.kira.Authservice.client.VendorServiceClient;
//import com.kira.userservice.repository.CustomerRepository;
import com.tekpyramid.kira.Authservice.dto.CustomerRequestDTO;
import com.tekpyramid.kira.Authservice.dto.LoginRequest;
import com.tekpyramid.kira.Authservice.dto.VendorRequestDTO;
import com.tekpyramid.kira.Authservice.entity.Auth;
import com.tekpyramid.kira.Authservice.repository.AuthRepository;
import com.tekpyramid.kira.Authservice.response.ApiResponse;
import com.tekpyramid.kira.Authservice.service.UserDetailsServiceImpl;
import com.tekpyramid.kira.Authservice.service.UserService;
import com.tekpyramid.kira.Authservice.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
//    private final CustomerServiceClient customerServiceClient;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
//    private final VendorServiceClient vendorServiceClient;
    private final UserService userService;

//    private final CustomerRepository customerRepository;


    @PostMapping("/auth/signup/customer")
    public ResponseEntity<ApiResponse<Auth>> registerCustomer(
            @Valid @RequestBody CustomerRequestDTO request) {

        ApiResponse<Auth> response = userService.registerCustomer(request);
        return ResponseEntity.ok(response);
    }



    @PostMapping("/auth/signup/vendor")
    public ResponseEntity<ApiResponse<Auth>> registerVendor(@Valid @RequestBody VendorRequestDTO request) {

        ApiResponse<Auth> response = userService.registerVendor(request);

//        if (response.getData() == null) {
//            return ResponseEntity.badRequest().body(response);
//        }

        return ResponseEntity.ok(response);
    }


    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest request) {

        ApiResponse<String> response = userService.login(request);

        if (response.getAccessToken() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return ResponseEntity.ok(response);
    }



//    @PostMapping("/verify-otp")
//    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestBody OtpVerifyRequest request) {
//
//        boolean isValid = otpService.validateOtp(request.getUserName(), request.getOtp());
//
//        if (!isValid) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(ApiResponse.<String>builder()
//                            .message("Invalid or expired OTP")
//                            .timestamp(LocalDateTime.now())
//                            .build());
//        }
//
//        var userDetails = userDetailsService.loadUserByUsername(request.getUserName());
//        String token = jwtUtils.generateToken(userDetails);
//
//        otpService.clearOtp(request.getUserName());
//
//        return ResponseEntity.ok(
//                ApiResponse.<String>builder()
//                        .message("OTP verified successfully!")
//                        .token(token)
//                        .timestamp(LocalDateTime.now())
//                        .build()
//        );
//    }


}
