package com.artnest.dto;

import com.artnest.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookingStatusRequest {
    @NotNull(message = "status is required")
    private BookingStatus status;
}
