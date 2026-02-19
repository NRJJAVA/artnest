package com.artnest.entity;

import com.artnest.enums.ArtistProfileStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "artist_profiles")
@Getter
@Setter
public class ArtistProfile extends  BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    private String bio;

    private String serviceCategory;
    private String location;

    private Double hourlyRate;

    private Integer experienceInYears;

    private String portfolioUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArtistProfileStatus status = ArtistProfileStatus.ACTIVE;

    @PrePersist
    public void applyDefaults() {
        if (status == null) {
            status = ArtistProfileStatus.ACTIVE;
        }
    }
}


