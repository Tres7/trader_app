package com.traderapp.modules.auth.infrastructure.persistence.repositories;

import com.traderapp.modules.auth.infrastructure.persistence.entities.PasswordResetCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataPasswordResetCodeRepository extends JpaRepository<PasswordResetCodeEntity, UUID> {

    Optional<PasswordResetCodeEntity> findByUserIdAndCode(UUID userId, String code);

    @Modifying
    @Query("""
        update PasswordResetCodeEntity e
        set e.used = true,
            e.usedAt = CURRENT_TIMESTAMP
        where e.userId = :userId
          and e.used = false
    """)
    void markAllAsUsedByUserId(@Param("userId") UUID userId);
}