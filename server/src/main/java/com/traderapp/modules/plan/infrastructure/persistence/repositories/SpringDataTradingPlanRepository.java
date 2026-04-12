package com.traderapp.modules.plan.infrastructure.persistence.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.traderapp.modules.plan.infrastructure.persistence.entities.TradingPlanEntity;

public interface SpringDataTradingPlanRepository extends JpaRepository<TradingPlanEntity, UUID> {
    Optional<TradingPlanEntity> findByUserId(UUID userId);
}
