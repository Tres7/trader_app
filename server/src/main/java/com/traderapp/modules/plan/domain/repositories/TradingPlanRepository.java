package com.traderapp.modules.plan.domain.repositories;

import java.util.Optional;
import java.util.UUID;

import com.traderapp.modules.plan.domain.entities.TradingPlan;
import com.traderapp.modules.plan.domain.valueObjects.TradingPlanId;

public interface TradingPlanRepository {
    Optional<TradingPlan> findByUserId(UUID userId);
    Optional<TradingPlan> findById(TradingPlanId id);
    TradingPlan save (TradingPlan plan);
}
