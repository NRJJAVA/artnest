package com.artnest.service.serviceImpl;

import com.artnest.dto.ArtistOnboardingRequest;
import com.artnest.dto.ArtistOnboardingResponse;
import com.artnest.dto.UserDetailsResponse;
import com.artnest.dto.UserRegisterResponse;
import com.artnest.dto.UsersRegisterRequest;
import com.artnest.entity.ArtistProfile;
import com.artnest.entity.UserRoles;
import com.artnest.entity.Users;
import com.artnest.enums.ArtistProfileStatus;
import com.artnest.enums.UserRole;
import com.artnest.exception.ConflictException;
import com.artnest.exception.ResourceNotFoundException;
import com.artnest.repository.ArtistProfileRepository;
import com.artnest.repository.UsersRepository;
import com.artnest.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UsersRepository usersRepository,
                           ArtistProfileRepository artistProfileRepository,
                           PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.artistProfileRepository = artistProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserRegisterResponse registerUser(UsersRegisterRequest request) {
        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("An account with this email already exists");
        }
        if (usersRepository.existsByPhone(request.getPhone())) {
            throw new ConflictException("An account with this mobile number already exists");
        }

        Users user = new Users();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setCountryCode(request.getCountryCode());
        user.setAddress(normalizeOptionalText(request.getAddress()));
        user.setGender(request.getGender());
        user.setDefaultMode(UserRole.CUSTOMER);
        user.setOnboardingCompleted(false);

        UserRoles customerRole = new UserRoles();
        customerRole.setUser(user);
        customerRole.setRole(UserRole.CUSTOMER);
        user.getRoles().add(customerRole);

        Users savedUser = usersRepository.save(user);

        UserRegisterResponse response = new UserRegisterResponse();
        response.setId(savedUser.getId());
        response.setFullName(savedUser.getFullName());
        response.setEmail(savedUser.getEmail());
        response.setPhone(savedUser.getPhone());
        response.setCountryCode(savedUser.getCountryCode());
        response.setAddress(savedUser.getAddress());
        response.setGender(savedUser.getGender());
        response.setRoles(getRoleNames(savedUser));
        response.setDefaultMode(savedUser.getDefaultMode());
        response.setOnboardingCompleted(savedUser.getOnboardingCompleted());
        return response;
    }

    @Override
    @Transactional
    public ArtistOnboardingResponse registerAsArtist(String userName, ArtistOnboardingRequest request) {
        Users user = usersRepository.findByEmail(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + userName));

        addRoleIfMissing(user, UserRole.ARTIST);

        ArtistProfile artistProfile = artistProfileRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    ArtistProfile profile = new ArtistProfile();
                    profile.setUser(user);
                    return profile;
                });

        artistProfile.setBio(request.getBio());
        artistProfile.setServiceCategory(request.getServiceCategory());
        artistProfile.setLocation(request.getLocation());
        artistProfile.setHourlyRate(request.getHourlyRate());
        artistProfile.setExperienceInYears(request.getExperienceInYears());
        artistProfile.setPortfolioUrl(request.getPortfolioUrl());
        artistProfile.setStatus(ArtistProfileStatus.ACTIVE);

        ArtistProfile savedArtistProfile = artistProfileRepository.save(artistProfile);

        if (Boolean.TRUE.equals(request.getSetAsDefaultMode())) {
            user.setDefaultMode(UserRole.ARTIST);
        }
        user.setOnboardingCompleted(isArtistProfileComplete(savedArtistProfile));
        usersRepository.save(user);

        return new ArtistOnboardingResponse(
                user.getId(),
                savedArtistProfile.getId(),
                getRoleNames(user),
                user.getDefaultMode(),
                user.getOnboardingCompleted(),
                isArtistProfileComplete(savedArtistProfile)
        );
    }

    @Override
    public UserDetailsResponse getUser(String userName) {
        Users user = usersRepository.findByEmail(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + userName));

        ArtistProfile artistProfile = artistProfileRepository.findByUser_Id(user.getId()).orElse(null);
        boolean hasArtistProfile = artistProfile != null;
        boolean artistProfileCompleted = hasArtistProfile && isArtistProfileComplete(artistProfile);

        return new UserDetailsResponse(
                Math.toIntExact(user.getId()),
                user.getFullName(),
                user.getEmail(),
                user.getCountryCode() + " " + user.getPhone(),
                user.getAddress(),
                user.getGender(),
                getRoleNames(user),
                user.getDefaultMode(),
                user.getOnboardingCompleted(),
                hasArtistProfile,
                artistProfileCompleted
        );
    }

    @Override
    @Transactional
    public UserDetailsResponse updateDefaultMode(String userName, UserRole defaultMode) {
        Users user = usersRepository.findByEmail(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + userName));

        boolean hasRole = user.getRoles().stream().anyMatch(role -> role.getRole() == defaultMode);
        if (!hasRole) {
            throw new ConflictException("Default mode can only be set to a role assigned to this user");
        }

        user.setDefaultMode(defaultMode);
        usersRepository.save(user);
        return getUser(userName);
    }

    private void addRoleIfMissing(Users user, UserRole role) {
        boolean exists = user.getRoles().stream().anyMatch(userRole -> userRole.getRole() == role);
        if (!exists) {
            UserRoles newRole = new UserRoles();
            newRole.setUser(user);
            newRole.setRole(role);
            user.getRoles().add(newRole);
        }
    }

    private List<String> getRoleNames(Users user) {
        return user.getRoles().stream().map(r -> r.getRole().name()).toList();
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

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
