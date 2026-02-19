package com.artnest.service.serviceImpl;

import com.artnest.config.CustomUserPrincipal;
import com.artnest.dto.LoginRequest;
import com.artnest.dto.LoginResponse;
import com.artnest.entity.ArtistProfile;
import com.artnest.entity.Users;
import com.artnest.enums.ArtistProfileStatus;
import com.artnest.exception.AuthenticationFailureException;
import com.artnest.exception.ResourceNotFoundException;
import com.artnest.repository.ArtistProfileRepository;
import com.artnest.repository.UsersRepository;
import com.artnest.service.AuthService;
import com.artnest.util.JwtTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UsersRepository usersRepository;
    private final ArtistProfileRepository artistProfileRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenUtil jwtTokenUtil,
                           UsersRepository usersRepository,
                           ArtistProfileRepository artistProfileRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.usersRepository = usersRepository;
        this.artistProfileRepository = artistProfileRepository;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmailOrPhone(),
                            request.getPassword()
                    )
            );

            CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(principal);

            Users user = usersRepository.findByEmail(principal.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + principal.getUsername()));

            ArtistProfile artistProfile = artistProfileRepository.findByUser_Id(user.getId()).orElse(null);
            boolean hasArtistProfile = artistProfile != null;
            boolean artistProfileCompleted = hasArtistProfile && isArtistProfileComplete(artistProfile);

            List<String> roles = user.getRoles().stream()
                    .map(role -> role.getRole().name())
                    .toList();

            return new LoginResponse(
                    token,
                    roles,
                    user.getDefaultMode(),
                    user.getOnboardingCompleted(),
                    hasArtistProfile,
                    artistProfileCompleted
            );
        } catch (BadCredentialsException ex) {
            throw new AuthenticationFailureException("Invalid email/phone or password");
        }
    }

    private boolean isArtistProfileComplete(ArtistProfile profile) {
        return profile.getStatus() == ArtistProfileStatus.ACTIVE
                && isNotBlank(profile.getBio())
                && isNotBlank(profile.getServiceCategory())
                && isNotBlank(profile.getLocation())
                && profile.getHourlyRate() != null
                && profile.getHourlyRate() > 0
                && profile.getExperienceInYears() != null
                && profile.getExperienceInYears() >= 0;
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
