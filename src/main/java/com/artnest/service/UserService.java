package com.artnest.service;

import com.artnest.dto.UserRegisterResponse;
import com.artnest.dto.UsersRegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserRegisterResponse registerUser(UsersRegisterRequest request);
}
