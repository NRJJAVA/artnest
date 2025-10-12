package com.artnest.service;

import com.artnest.dto.LoginRequest;
import com.artnest.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    LoginResponse login(@Valid LoginRequest request);
}
