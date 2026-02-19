package com.artnest.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateBookingRequest {

    @NotNull(message = "artistId is required")
    private Long artistId;

    @NotNull(message = "bookingDate is required")
    @Future(message = "bookingDate must be in the future")
    private LocalDateTime bookingDate;

    @Min(value = 60, message = "durationMinutes should be at least 60")
    @Max(value = 240, message = "durationMinutes should be less than or equal to 240")
    private Integer durationMinutes;

    @NotBlank(message = "artType is required")
    private String artType;

    @NotBlank(message = "location is required")
    private String location;

    @NotBlank(message = "description is required")
    private String description;
}
