package com.artnest.entity;

import com.artnest.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Users customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Users artist;

    @Column(nullable = false)
    private LocalDateTime bookingDate;

    private LocalDateTime bookingEndDate;

    private Integer durationMinutes;

    @Column(nullable = false)
    private String artType;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @PrePersist
    @PreUpdate
    public void applyDefaults() {
        if (durationMinutes == null || durationMinutes <= 0) {
            durationMinutes = 60;
        }
        if (bookingDate != null && bookingEndDate == null) {
            bookingEndDate = bookingDate.plusMinutes(durationMinutes);
        }
    }
}

