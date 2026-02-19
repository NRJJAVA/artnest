package com.artnest.repository;

import com.artnest.entity.ArtistProfile;
import com.artnest.enums.ArtistProfileStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistProfileRepository extends JpaRepository<ArtistProfile, Long> {
    boolean existsByUser_Id(Long userId);

    Optional<ArtistProfile> findByUser_Id(Long userId);

    List<ArtistProfile> findByStatus(ArtistProfileStatus status);

    List<ArtistProfile> findByStatusAndServiceCategoryContainingIgnoreCase(
            ArtistProfileStatus status,
            String serviceCategory
    );

    Page<ArtistProfile> findByStatus(ArtistProfileStatus status, Pageable pageable);

    Page<ArtistProfile> findByStatusAndServiceCategoryContainingIgnoreCase(
            ArtistProfileStatus status,
            String serviceCategory,
            Pageable pageable
    );
}
