package com.artnest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
public class UpsertArtistAvailabilityRequest {

    @NotNull(message = "dayOfWeek is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "startTime is required")
    private LocalTime startTime;

    @NotNull(message = "endTime is required")
    private LocalTime endTime;

    @Min(value = 15, message = "slotDurationMinutes should be at least 15")
    @Max(value = 480, message = "slotDurationMinutes should be less than or equal to 480")
    private Integer slotDurationMinutes;

    private Boolean active;
}
