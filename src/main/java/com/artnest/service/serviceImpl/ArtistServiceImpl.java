package com.artnest.service.serviceImpl;

import com.artnest.dto.ArtistProfileSearchResponse;
import com.artnest.dto.ArtistSearchPageResponse;
import com.artnest.entity.ArtistProfile;
import com.artnest.enums.ArtistProfileStatus;
import com.artnest.repository.ArtistProfileRepository;
import com.artnest.service.ArtistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.List;

@Service
public class ArtistServiceImpl implements ArtistService {

    private final ArtistProfileRepository artistProfileRepository;

    public ArtistServiceImpl(ArtistProfileRepository artistProfileRepository) {
        this.artistProfileRepository = artistProfileRepository;
    }

    @Override
    public ArtistSearchPageResponse searchArtists(String artType, Integer page, Integer size, String sortBy, String sortDir) {
        int pageNo = page == null || page < 0 ? 0 : page;
        int pageSize = size == null || size <= 0 ? 10 : Math.min(size, 50);

        String normalizedSortBy = normalizeSortBy(sortBy);
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(direction, normalizedSortBy));

        Page<ArtistProfile> profiles;
        if (artType == null || artType.trim().isEmpty()) {
            profiles = artistProfileRepository.findByStatus(ArtistProfileStatus.ACTIVE, pageable);
        } else {
            profiles = artistProfileRepository.findByStatusAndServiceCategoryContainingIgnoreCase(
                    ArtistProfileStatus.ACTIVE,
                    artType.trim(),
                    pageable
            );
        }

        List<ArtistProfileSearchResponse> items = profiles.stream()
                .map(profile -> new ArtistProfileSearchResponse(
                        profile.getId(),
                        profile.getUser().getId(),
                        profile.getUser().getFullName(),
                        profile.getUser().getProfileImageUrl(),
                        profile.getBio(),
                        profile.getServiceCategory(),
                        profile.getLocation(),
                        profile.getHourlyRate(),
                        profile.getExperienceInYears(),
                        profile.getPortfolioUrl()
                ))
                .toList();

        return new ArtistSearchPageResponse(
                items,
                profiles.getNumber(),
                profiles.getSize(),
                profiles.getTotalElements(),
                profiles.getTotalPages(),
                normalizedSortBy,
                direction.name().toLowerCase(),
                artType
        );
    }

    private String normalizeSortBy(String sortBy) {
        Map<String, String> allowedSortFields = Map.of(
                "hourlyRate", "hourlyRate",
                "experienceInYears", "experienceInYears",
                "createdAt", "createdAt",
                "serviceCategory", "serviceCategory",
                "location", "location"
        );
        return allowedSortFields.getOrDefault(sortBy, "createdAt");
    }
}
