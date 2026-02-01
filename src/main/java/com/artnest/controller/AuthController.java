package com.artnest.controller;

import com.artnest.dto.*;
import com.artnest.service.UserService;
import com.artnest.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

   final  private UserService userService;


    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserRegisterResponse>> register(
            @Valid @RequestBody UsersRegisterRequest request) {

        UserRegisterResponse response = userService.registerUser(request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "User registered successfully", response)
        );
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
    }
}

