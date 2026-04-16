package com.traderapp.modules.notification.infrastructure.persistence.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.traderapp.modules.notification.infrastructure.persistence.entities.NotificationEntity;

public interface SpringDataNotificationRepository extends JpaRepository<NotificationEntity, UUID>{
   List<NotificationEntity> findByUserIdAndReadFalse(UUID userId);
    
   @Modifying
   @Query("UPDATE NotificationEntity n SET n.read = true WHERE n.id = :id")
   void markAsRead(@Param("id") UUID id);

    
}
