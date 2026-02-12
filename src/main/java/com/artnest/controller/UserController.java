package com.artnest.controller;


import com.artnest.dto.ApiResponse;
import com.artnest.dto.UserDetailsResponse;
import com.artnest.dto.UserRegisterResponse;
import com.artnest.dto.UsersRegisterRequest;
import com.artnest.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/me")
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
}

