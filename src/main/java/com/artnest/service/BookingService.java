package com.artnest.service;

import com.artnest.dto.BookingResponse;
import com.artnest.dto.CreateBookingRequest;
import com.artnest.enums.BookingStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookingService {
    BookingResponse createBooking(String requesterEmail, CreateBookingRequest request);

    List<BookingResponse> getCustomerBookings(String requesterEmail);

    List<BookingResponse> getArtistBookings(String requesterEmail);

    BookingResponse updateBookingStatus(String requesterEmail, Long bookingId, BookingStatus status);

    BookingResponse extendBooking(String requesterEmail, Long bookingId, Integer additionalMinutes);
}
