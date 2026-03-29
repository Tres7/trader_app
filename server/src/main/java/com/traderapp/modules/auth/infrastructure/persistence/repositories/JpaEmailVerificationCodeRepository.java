package com.traderapp.modules.auth.infrastructure.persistence.repositories;

import com.traderapp.modules.auth.domain.entities.EmailVerificationCode;
import com.traderapp.modules.auth.domain.repositories.EmailVerificationCodeRepository;
import com.traderapp.modules.auth.infrastructure.persistence.entities.EmailVerificationCodeEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaEmailVerificationCodeRepository implements EmailVerificationCodeRepository {

    private final SpringDataEmailVerificationCodeRepository springDataRepository;

    public JpaEmailVerificationCodeRepository(SpringDataEmailVerificationCodeRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public EmailVerificationCode save(EmailVerificationCode emailVerificationCode) {
        EmailVerificationCodeEntity entity = toEntity(emailVerificationCode);
        EmailVerificationCodeEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<EmailVerificationCode> findByUserIdAndCode(UUID userId, String code) {
        return springDataRepository.findByUserIdAndCode(userId, code)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public void markAllAsUsedByUserId(UUID userId) {
        springDataRepository.markAllAsUsedByUserId(userId);
    }


    private EmailVerificationCodeEntity toEntity(EmailVerificationCode emailVerificationCode) {
        return new EmailVerificationCodeEntity(
                emailVerificationCode.getId(),
                emailVerificationCode.getUserId(),
                emailVerificationCode.getCode(),
                emailVerificationCode.getExpiresAt(),
                emailVerificationCode.getCreatedAt(),
                emailVerificationCode.isUsed(),
                emailVerificationCode.getUsedAt()
        );
    }

    private EmailVerificationCode toDomain(EmailVerificationCodeEntity entity) {
        return new EmailVerificationCode(
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
