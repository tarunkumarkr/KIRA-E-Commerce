package com.kira.userservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorResponseDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String businessName;
    private String gstNumber;


}
