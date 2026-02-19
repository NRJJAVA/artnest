package com.artnest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtistProfileSearchResponse {
    private Long artistProfileId;
    private Long userId;
    private String fullName;
    private String profileImageUrl;
    private String bio;
    private String serviceCategory;
    private String location;
    private Double hourlyRate;
    private Integer experienceInYears;
    private String portfolioUrl;
}
