package com.artnest.repository;

import com.artnest.entity.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    boolean existsByEmail(@Email(message = "Email should be valid") @NotBlank(message = "Email is required") String email);

    boolean existsByPhone(String phone);

    Optional<Users> findByEmailOrPhone(String emailOrPhone, String emailOrPhone1);
}
