package com.traderapp.modules.notification.infrastructure.persistence.repositories;


import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.traderapp.modules.notification.domain.entities.Notification;
import com.traderapp.modules.notification.domain.repositories.NotificationRepository;
import com.traderapp.modules.notification.infrastructure.persistence.entities.NotificationEntity;
import java.util.List;

import jakarta.transaction.Transactional;


@Repository
public class JpaNotificationRepository implements NotificationRepository {
    private SpringDataNotificationRepository springDataNotificationRepository;

    public JpaNotificationRepository(SpringDataNotificationRepository springDataNotificationRepository) {
        this.springDataNotificationRepository = springDataNotificationRepository;
    }
    @Override
    public Notification save(Notification notification) {
        NotificationEntity saved = springDataNotificationRepository.save(toEntity(notification));
        return toDomain(saved);
    }

    @Override
    public List<Notification> findUnreadByUserId(UUID userId) {
        return springDataNotificationRepository.findByUserIdAndReadFalse(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId) {
        springDataNotificationRepository.markAsRead(notificationId);
    }

    private NotificationEntity toEntity(Notification notification) {
        return new NotificationEntity(
                notification.getId(),
                notification.getUserId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }

    private Notification toDomain(NotificationEntity entity) {
        return new Notification(
                entity.getId(),
                entity.getUserId(),
                entity.getType(),
                entity.getTitle(),
                entity.getMessage(),
                entity.isRead(),
                entity.getCreatedAt()
        );
    }
}
