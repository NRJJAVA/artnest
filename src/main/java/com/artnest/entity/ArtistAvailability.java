package com.artnest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "artist_availability")
@Getter
@Setter
public class ArtistAvailability extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Users artist;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    private Integer slotDurationMinutes;

    @Column(nullable = false)
    private Boolean isActive = true;

    @PrePersist
    public void applyDefaults() {
        if (slotDurationMinutes == null || slotDurationMinutes <= 0) {
            slotDurationMinutes = 60;
        }
        if (isActive == null) {
            isActive = true;
        }
    }
}
