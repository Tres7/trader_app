package com.traderapp.modules.auth.infrastructure.persistence.repositories;

import com.traderapp.modules.auth.domain.entities.PasswordResetCode;
import com.traderapp.modules.auth.domain.repositories.PasswordResetCodeRepository;
import com.traderapp.modules.auth.infrastructure.persistence.entities.PasswordResetCodeEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaPasswordResetCodeRepository implements PasswordResetCodeRepository {

    private final SpringDataPasswordResetCodeRepository springDataRepository;

    public JpaPasswordResetCodeRepository(SpringDataPasswordResetCodeRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public PasswordResetCode save(PasswordResetCode passwordResetCode) {
        PasswordResetCodeEntity entity = toEntity(passwordResetCode);
        PasswordResetCodeEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<PasswordResetCode> findByUserIdAndCode(UUID userId, String code) {
        return springDataRepository.findByUserIdAndCode(userId, code)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public void markAllAsUsedByUserId(UUID userId) {
        springDataRepository.markAllAsUsedByUserId(userId);
    }

    private PasswordResetCodeEntity toEntity(PasswordResetCode passwordResetCode) {
        return new PasswordResetCodeEntity(
                passwordResetCode.getId(),
                passwordResetCode.getUserId(),
                passwordResetCode.getCode(),
                passwordResetCode.getExpiresAt(),
                passwordResetCode.getCreatedAt(),
                passwordResetCode.isUsed(),
                passwordResetCode.getUsedAt()
        );
    }

    private PasswordResetCode toDomain(PasswordResetCodeEntity entity) {
        return new PasswordResetCode(
                entity.getId(),
                entity.getUserId(),
                entity.getCode(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.isUsed(),
                entity.getUsedAt()
        );
    }
}