package com.artnest.service.serviceImpl;

import com.artnest.dto.UserDetailsResponse;
import com.artnest.dto.UserRegisterResponse;
import com.artnest.dto.UsersRegisterRequest;
import com.artnest.entity.UserRoles;
import com.artnest.entity.Users;
import com.artnest.enums.UserRole;
import com.artnest.repository.UsersRepository;
import com.artnest.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    private final UsersRepository usersRepository;


    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

        UserRoles customerRole = new UserRoles();
        customerRole.setUser(user);
        customerRole.setRole(UserRole.CUSTOMER);

        user.getRoles().add(customerRole);

        Users savedUser = usersRepository.save(user);

        // ✅ Build response
        UserRegisterResponse response = new UserRegisterResponse();
        response.setId(savedUser.getId());
        response.setFullName(savedUser.getFullName());
        response.setEmail(savedUser.getEmail());
        response.setPhone(savedUser.getPhone());
        response.setCountryCode(savedUser.getCountryCode());
        response.setRoles(
                savedUser.getRoles().stream()
                        .map(r -> r.getRole().name())
                        .toList()
        );

        return response;
    }


    @Override
    public UserDetailsResponse getUser(String userId) {

        Optional<Users> userDetails =usersRepository.findByEmail(userId);

        if(userDetails.isEmpty()){
            throw new EntityNotFoundException();
        }

        UserDetailsResponse response = new UserDetailsResponse();
        Users user = userDetails.get();
         response.setName(user.getFullName());
         response.setMobile(user.getCountryCode()+ " "+user.getPhone());
         response.setEmail(user.getEmail());
         response.setUserId(Math.toIntExact(user.getId()));



        return response;
    }


}

