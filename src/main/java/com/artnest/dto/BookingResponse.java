package com.artnest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.artnest.enums.Gender;
import com.artnest.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private Long customerId;
    private String customerName;
    private String customerAddress;
    private Gender customerGender;
    private Long artistId;
    private String artistName;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm a")
    private LocalDateTime bookingDate;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm a")
    private LocalDateTime bookingEndDate;
    private Integer durationMinutes;
    private String artType;
    private String location;
    private String description;
    private BookingStatus status;
    private LocalDateTime createdAt;
}
