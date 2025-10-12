package com.artnest.dto;


import com.artnest.enums.UserRole;
import lombok.Data;

@Data
public class UserRegisterResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String countryCode;
    private UserRole role;
}

