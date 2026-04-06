package com.traderapp.modules.auth.domain.repositories;

import java.util.Optional;

import com.traderapp.modules.auth.domain.entities.PasswordResetCode;
import java.util.UUID;

public interface PasswordResetCodeRepository {
    PasswordResetCode save(PasswordResetCode passwordResetCode);

    Optional<PasswordResetCode> findByUserIdAndCode(UUID userId, String code);

    void markAllAsUsedByUserId(UUID userId);
}
