package com.artnest.service;

import com.artnest.config.CustomUserPrincipal;
import com.artnest.entity.Users;
import com.artnest.repository.UsersRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {

        Users user = usersRepository.findByEmailOrPhone(emailOrPhone, emailOrPhone)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<SimpleGrantedAuthority> authorities =
                user.getRoles().stream()
                        .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().name()))
                        .toList();

        return new CustomUserPrincipal(user, authorities);
    }
}
