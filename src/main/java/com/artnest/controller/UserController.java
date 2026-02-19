package com.artnest.controller;

import com.artnest.dto.ArtistOnboardingRequest;
import com.artnest.dto.ArtistOnboardingResponse;
import com.artnest.dto.ApiResponse;
import com.artnest.dto.UpdateDefaultModeRequest;
import com.artnest.dto.UserDetailsResponse;
import com.artnest.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDetailsResponse>> getUserDetails() {

        String userName = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        UserDetailsResponse response = userService.getUser(userName);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "user details fetched successfully", response)
        );
    }

    @PostMapping("/register-artist")
    public ResponseEntity<ApiResponse<ArtistOnboardingResponse>> registerAsArtist(
            @Valid @RequestBody ArtistOnboardingRequest request) {

        String userName = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        ArtistOnboardingResponse response = userService.registerAsArtist(userName, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Artist capability enabled successfully.",
                        response
                )
        );
    }

    @PatchMapping("/default-mode")
    public ResponseEntity<ApiResponse<UserDetailsResponse>> updateDefaultMode(
            @Valid @RequestBody UpdateDefaultModeRequest request) {

        String userName = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        UserDetailsResponse response = userService.updateDefaultMode(userName, request.getDefaultMode());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Default mode updated successfully", response)
        );
    }

}

