package com.artnest.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "artist_profiles")
public class ArtistProfile extends  BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private String bio;

    private String serviceCategory;
    private String location;

    private Double hourlyRate;

    private Integer experienceInYears;

    private String portfolioUrl;
}


