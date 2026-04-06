package com.traderapp.modules.auth.domain.repositories;

import java.util.Optional;
import java.util.UUID;

import com.traderapp.modules.auth.domain.entities.EmailVerificationCode;

public interface EmailVerificationCodeRepository {
    EmailVerificationCode save(EmailVerificationCode emailVerificationCode);

    Optional<EmailVerificationCode> findByUserIdAndCode(UUID userId, String code);

    void markAllAsUsedByUserId(java.util.UUID userId);
    
}
