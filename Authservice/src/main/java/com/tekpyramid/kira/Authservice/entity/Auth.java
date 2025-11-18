package com.tekpyramid.kira.Authservice.entity;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "auth_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auth {

    @Id
    private String id;

    @NotBlank(message = "Username is required")
    private String userName;
    @NotBlank(message = "Password is required ")
    private String password;
    @NotBlank(message = "Email is required ")
    private String email;
    @NotNull(message = "Role is required ")
    private Role role;

    private boolean active = true;
}
