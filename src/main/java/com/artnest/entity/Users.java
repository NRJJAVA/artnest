package com.artnest.entity;

import com.artnest.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String profileImageUrl;

}


