package com.artnest.repository;

import com.artnest.entity.ArtistTimeOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArtistTimeOffRepository extends JpaRepository<ArtistTimeOff, Long> {
    List<ArtistTimeOff> findByArtist_IdOrderByStartDateTimeDesc(Long artistId);

    List<ArtistTimeOff> findByArtist_IdAndEndDateTimeAfterAndStartDateTimeBefore(
            Long artistId,
            LocalDateTime windowStart,
            LocalDateTime windowEnd
    );
}
