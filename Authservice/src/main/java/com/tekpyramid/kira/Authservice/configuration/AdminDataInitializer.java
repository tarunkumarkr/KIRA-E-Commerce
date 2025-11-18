package com.tekpyramid.kira.Authservice.configuration;

import com.tekpyramid.kira.Authservice.entity.Auth;
import com.tekpyramid.kira.Authservice.entity.Role;
import com.tekpyramid.kira.Authservice.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminDataInitializer {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner createAdmin() {
        return args -> {

            String adminUserName = "admin";
            boolean exists = authRepository.findByUserName(adminUserName).isPresent();

            if (!exists) {

                Auth admin = Auth.builder()
                        .userName(adminUserName)
                        .email("admin@kira.com")
                        .password(passwordEncoder.encode("Admin@123"))
                        .role(Role.ADMIN)      // ✔ enum role (NO DB NEEDED)
                        .active(true)
                        .build();

                authRepository.save(admin);

                System.out.println("🔥 Admin Created!");
                System.out.println("Username: admin");
                System.out.println("Password: Admin@123");

            } else {
                System.out.println("✔ Admin already exists!");
            }
        };
    }
}
