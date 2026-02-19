package com.artnest.controller;

import com.artnest.dto.ApiResponse;
import com.artnest.dto.ArtistAvailabilityResponse;
import com.artnest.dto.ArtistTimeOffResponse;
import com.artnest.dto.CreateArtistTimeOffRequest;
import com.artnest.dto.UpsertArtistAvailabilityRequest;
import com.artnest.service.ArtistScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/artist-schedule")
public class ArtistScheduleController {

    private final ArtistScheduleService artistScheduleService;

    public ArtistScheduleController(ArtistScheduleService artistScheduleService) {
        this.artistScheduleService = artistScheduleService;
    }

    @PostMapping("/availability")
    public ResponseEntity<ApiResponse<ArtistAvailabilityResponse>> upsertAvailability(
            @Valid @RequestBody UpsertArtistAvailabilityRequest request) {
        String requesterEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ArtistAvailabilityResponse response = artistScheduleService.upsertAvailability(requesterEmail, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Availability saved successfully", response));
    }

    @GetMapping("/availability")
    public ResponseEntity<ApiResponse<List<ArtistAvailabilityResponse>>> getMyAvailabilities() {
        String requesterEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ArtistAvailabilityResponse> response = artistScheduleService.getMyAvailabilities(requesterEmail);
        return ResponseEntity.ok(new ApiResponse<>(true, "Availabilities fetched successfully", response));
    }

    @PostMapping("/time-off")
    public ResponseEntity<ApiResponse<ArtistTimeOffResponse>> createTimeOff(
            @Valid @RequestBody CreateArtistTimeOffRequest request) {
        String requesterEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        ArtistTimeOffResponse response = artistScheduleService.createTimeOff(requesterEmail, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Time-off saved successfully", response));
    }

    @GetMapping("/time-off")
    public ResponseEntity<ApiResponse<List<ArtistTimeOffResponse>>> getMyTimeOff() {
        String requesterEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ArtistTimeOffResponse> response = artistScheduleService.getMyTimeOff(requesterEmail);
        return ResponseEntity.ok(new ApiResponse<>(true, "Time-off fetched successfully", response));
    }
}
