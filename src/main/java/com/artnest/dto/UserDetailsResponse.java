package com.artnest.dto;

import com.artnest.enums.Gender;
import com.artnest.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResponse {

    private Integer userId;
    private String name;
    private String email;
    private String mobile;
    private String address;
    private Gender gender;
    private List<String> roles;
    private UserRole defaultMode;
    private Boolean onboardingCompleted;
    private Boolean hasArtistProfile;
    private Boolean artistProfileCompleted;
}
