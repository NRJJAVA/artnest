package com.artnest.entity;

import com.artnest.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Setter
@Getter
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String countryCode;

    @Column(unique = true, nullable = false)
    private String phone;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRoles> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole defaultMode = UserRole.CUSTOMER;

    @Column(nullable = false)
    private Boolean onboardingCompleted = false;

    @Column(columnDefinition = "TEXT")
    private String preferences;

    private String profileImageUrl;

    @PrePersist
    public void applyDefaults() {
        if (defaultMode == null) {
            defaultMode = UserRole.CUSTOMER;
        }
        if (onboardingCompleted == null) {
            onboardingCompleted = false;
        }
    }

}


