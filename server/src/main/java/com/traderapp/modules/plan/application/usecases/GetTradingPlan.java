package com.traderapp.modules.plan.application.usecases;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.traderapp.modules.plan.domain.entities.TradingPlan;
import com.traderapp.modules.plan.domain.repositories.TradingPlanRepository;

@Service
public class GetTradingPlan {
    private final TradingPlanRepository tradingPlanRepository;

    public GetTradingPlan (TradingPlanRepository tradingPlanRepository) {
        this.tradingPlanRepository = tradingPlanRepository;
    }

    public Optional<TradingPlan> execute(UUID userId) {
       return tradingPlanRepository.findByUserId(userId);
    }
}
