package com.tekpyramid.kira_product_inventory.dto;

import com.tekpyramid.kira_product_inventory.entity.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;

@Data
public class VendorDto extends BaseEntity {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String businessName;
    private String gstNumber;
    private String businessAddress;
    private Double rating;
    private String role = "VENDOR";
    private boolean status = true;


}

