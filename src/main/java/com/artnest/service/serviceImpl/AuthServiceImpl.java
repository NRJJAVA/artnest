package com.artnest.service.serviceImpl;

import com.artnest.dto.LoginRequest;
import com.artnest.dto.LoginResponse;
import com.artnest.service.AuthService;
import com.artnest.service.CustomUserDetailsService;
import com.artnest.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {


        private final AuthenticationManager authenticationManager;

        private final JwtTokenUtil jwtTokenUtil;

        private final CustomUserDetailsService userDetailsService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmailOrPhone(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmailOrPhone());

        // Generate JWT token
        String token = jwtTokenUtil.generateToken(userDetails);

        return new LoginResponse(token);
    }

}
