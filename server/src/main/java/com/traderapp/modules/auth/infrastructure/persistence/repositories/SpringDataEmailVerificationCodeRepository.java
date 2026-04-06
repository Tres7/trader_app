package com.traderapp.modules.auth.infrastructure.persistence.repositories;

import com.traderapp.modules.auth.infrastructure.persistence.entities.EmailVerificationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataEmailVerificationCodeRepository extends JpaRepository<EmailVerificationCodeEntity, UUID> {

    Optional<EmailVerificationCodeEntity> findByUserIdAndCode(UUID userId, String code);
    
    @Modifying
    @Query("""
    update EmailVerificationCodeEntity e
    set e.used = true,
        e.usedAt = CURRENT_TIMESTAMP
    where e.userId = :userId
      and e.used = false
    """)
    void markAllAsUsedByUserId(@Param("userId") UUID userId);
}