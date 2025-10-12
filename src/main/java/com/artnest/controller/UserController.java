//package com.artnest.controller;
//
//
//import com.artnest.dto.ApiResponse;
//import com.artnest.dto.UserRegisterResponse;
//import com.artnest.dto.UsersRegisterRequest;
//import com.artnest.service.UserService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    @PostMapping("/register")
//    public ResponseEntity<ApiResponse<UserRegisterResponse>> register(
//            @Valid @RequestBody UsersRegisterRequest request) {
//
//        UserRegisterResponse response = userService.registerUser(request);
//
//        return ResponseEntity.ok(
//                new ApiResponse<>(true, "User registered successfully", response)
//        );
//    }
//}
//
