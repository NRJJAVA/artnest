package com.artnest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtendBookingRequest {
    @NotNull(message = "additionalMinutes is required")
    @Min(value = 30, message = "additionalMinutes should be at least 30")
    @Max(value = 180, message = "additionalMinutes should be less than or equal to 180")
    private Integer additionalMinutes;
}
