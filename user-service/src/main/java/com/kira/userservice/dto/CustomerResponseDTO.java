package com.kira.userservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

}
