package com.artnest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtistOnboardingRequest {

    @NotBlank(message = "Bio is required")
    private String bio;

    @NotBlank(message = "Service category is required")
    private String serviceCategory;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Hourly rate is required")
    @DecimalMin(value = "0.01", message = "Hourly rate must be greater than 0")
    private Double hourlyRate;

    @NotNull(message = "Experience in years is required")
    @Min(value = 0, message = "Experience in years cannot be negative")
    private Integer experienceInYears;

    private String portfolioUrl;

    private Boolean setAsDefaultMode;
}
