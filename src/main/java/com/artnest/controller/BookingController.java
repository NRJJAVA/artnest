package com.artnest.controller;

import com.artnest.dto.ApiResponse;
import com.artnest.dto.BookingResponse;
import com.artnest.dto.CreateBookingRequest;
import com.artnest.dto.ExtendBookingRequest;
import com.artnest.dto.UpdateBookingStatusRequest;
import com.artnest.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {
        String requesterEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        BookingResponse response = bookingService.createBooking(requesterEmail, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Booking created successfully", response));
    }

    @GetMapping("/customer")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getCustomerBookings() {
        String requesterEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<BookingResponse> response = bookingService.getCustomerBookings(requesterEmail);
        return ResponseEntity.ok(new ApiResponse<>(true, "Customer bookings fetched successfully", response));
    }

    @GetMapping("/artist")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getArtistBookings() {
        String requesterEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<BookingResponse> response = bookingService.getArtistBookings(requesterEmail);
        return ResponseEntity.ok(new ApiResponse<>(true, "Artist bookings fetched successfully", response));
    }

    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<ApiResponse<BookingResponse>> updateBookingStatus(
            @PathVariable Long bookingId,
            @Valid @RequestBody UpdateBookingStatusRequest request) {
        String requesterEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        BookingResponse response = bookingService.updateBookingStatus(requesterEmail, bookingId, request.getStatus());
        return ResponseEntity.ok(new ApiResponse<>(true, "Booking status updated successfully", response));
    }

    @PatchMapping("/{bookingId}/extend")
    public ResponseEntity<ApiResponse<BookingResponse>> extendBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody ExtendBookingRequest request) {
        String requesterEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        BookingResponse response = bookingService.extendBooking(requesterEmail, bookingId, request.getAdditionalMinutes());
        return ResponseEntity.ok(new ApiResponse<>(true, "Booking extended successfully", response));
    }
}
