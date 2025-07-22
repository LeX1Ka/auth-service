package org.example.authservice.repository;

import org.example.authservice.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, UUID> {
    public Optional<VerificationCode> findByVerificationCode(String code);
    public Optional<VerificationCode> findByEmail(String email);
    void deleteAllByExpiresAtBefore(LocalDateTime timestamp);
}
