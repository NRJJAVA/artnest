package com.artnest.dto;

import com.artnest.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtistOnboardingResponse {
    private Long userId;
    private Long artistProfileId;
    private List<String> roles;
    private UserRole defaultMode;
    private Boolean onboardingCompleted;
    private Boolean artistProfileCompleted;
}
