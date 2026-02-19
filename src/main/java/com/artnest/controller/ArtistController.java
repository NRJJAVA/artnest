package com.artnest.controller;

import com.artnest.dto.ApiResponse;
import com.artnest.dto.ArtistSearchPageResponse;
import com.artnest.dto.AvailableSlotResponse;
import com.artnest.service.ArtistScheduleService;
import com.artnest.service.ArtistService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;
    private final ArtistScheduleService artistScheduleService;

    public ArtistController(ArtistService artistService, ArtistScheduleService artistScheduleService) {
        this.artistService = artistService;
        this.artistScheduleService = artistScheduleService;
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ArtistSearchPageResponse>> searchArtists(
            @RequestParam(required = false) String artType,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        ArtistSearchPageResponse response = artistService.searchArtists(artType, page, size, sortBy, sortDir);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Artists fetched successfully", response)
        );
    }

    @GetMapping("/{artistId}/available-slots")
    public ResponseEntity<ApiResponse<List<AvailableSlotResponse>>> getAvailableSlots(
            @PathVariable Long artistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AvailableSlotResponse> response = artistScheduleService.getAvailableSlots(artistId, date);
        return ResponseEntity.ok(new ApiResponse<>(true, "Available slots fetched successfully", response));
    }
}
