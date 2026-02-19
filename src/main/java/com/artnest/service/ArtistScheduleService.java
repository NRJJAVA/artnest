package com.artnest.service;

import com.artnest.dto.ArtistAvailabilityResponse;
import com.artnest.dto.ArtistTimeOffResponse;
import com.artnest.dto.AvailableSlotResponse;
import com.artnest.dto.CreateArtistTimeOffRequest;
import com.artnest.dto.UpsertArtistAvailabilityRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface ArtistScheduleService {
    ArtistAvailabilityResponse upsertAvailability(String requesterEmail, UpsertArtistAvailabilityRequest request);

    List<ArtistAvailabilityResponse> getMyAvailabilities(String requesterEmail);

    ArtistTimeOffResponse createTimeOff(String requesterEmail, CreateArtistTimeOffRequest request);

    List<ArtistTimeOffResponse> getMyTimeOff(String requesterEmail);

    List<AvailableSlotResponse> getAvailableSlots(Long artistId, LocalDate date);
}
