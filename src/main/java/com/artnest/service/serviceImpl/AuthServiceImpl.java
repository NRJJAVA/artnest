package com.artnest.service.serviceImpl;

import com.artnest.config.CustomUserPrincipal;
import com.artnest.dto.LoginRequest;
import com.artnest.dto.LoginResponse;
import com.artnest.service.AuthService;
import com.artnest.service.CustomUserDetailsService;
import com.artnest.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {


        private final AuthenticationManager authenticationManager;

        private final JwtTokenUtil jwtTokenUtil;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }
    public LoginResponse login(LoginRequest request) {

        System.out.println("1");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmailOrPhone(),
                        request.getPassword()
                )
        );

        System.out.println("2");
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        String token = jwtTokenUtil.generateToken(principal);

        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new LoginResponse(token);

    }

}
