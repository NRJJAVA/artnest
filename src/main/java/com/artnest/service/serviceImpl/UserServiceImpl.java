package com.artnest.service.serviceImpl;

import com.artnest.dto.UserRegisterResponse;
import com.artnest.dto.UsersRegisterRequest;
import com.artnest.entity.Users;
import com.artnest.enums.UserRole;
import com.artnest.repository.UsersRepository;
import com.artnest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserRegisterResponse registerUser(UsersRegisterRequest request) {

        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        Users user = new Users();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setCountryCode(request.getCountryCode());
        user.setRole(UserRole.CUSTOMER);

        Users savedUser = usersRepository.save(user);

        UserRegisterResponse response = new UserRegisterResponse();
        response.setId(savedUser.getId());
        response.setFullName(savedUser.getFullName());
        response.setEmail(savedUser.getEmail());
        response.setPhone(savedUser.getPhone());
        response.setCountryCode(savedUser.getCountryCode());
        response.setRole(savedUser.getRole());

        return response;
    }


}

