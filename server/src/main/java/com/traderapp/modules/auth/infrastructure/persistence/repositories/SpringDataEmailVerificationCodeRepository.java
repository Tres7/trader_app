package com.traderapp.modules.auth.infrastructure.persistence.repositories;

import com.traderapp.modules.auth.infrastructure.persistence.entities.EmailVerificationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataEmailVerificationCodeRepository extends JpaRepository<EmailVerificationCodeEntity, UUID> {

    Optional<EmailVerificationCodeEntity> findByUserIdAndCode(UUID userId, String code);
}