package com.tekpyramid.kira.Authservice.repository;

import com.tekpyramid.kira.Authservice.entity.Auth;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends MongoRepository<Auth, String> {

    // 🔹 Find user by username (for login)
    Optional<Auth> findByUserName(String userName);

    // 🔹 Check if a username already exists (for registration validation)
    boolean existsByUserName(String userName);

    Optional<Auth> findByEmail(@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email);

    boolean existsByEmail(@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email);
}

