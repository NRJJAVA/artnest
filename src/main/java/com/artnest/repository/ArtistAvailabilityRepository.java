package com.artnest.repository;

import com.artnest.entity.ArtistAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface ArtistAvailabilityRepository extends JpaRepository<ArtistAvailability, Long> {
    List<ArtistAvailability> findByArtist_IdOrderByDayOfWeekAscStartTimeAsc(Long artistId);

    List<ArtistAvailability> findByArtist_IdAndIsActiveTrueAndDayOfWeekOrderByStartTimeAsc(Long artistId, DayOfWeek dayOfWeek);
}
