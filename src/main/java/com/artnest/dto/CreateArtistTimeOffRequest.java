package com.artnest.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateArtistTimeOffRequest {

    @NotNull(message = "startDateTime is required")
    @Future(message = "startDateTime must be in the future")
    private LocalDateTime startDateTime;

    @NotNull(message = "endDateTime is required")
    private LocalDateTime endDateTime;

    private String reason;
}
