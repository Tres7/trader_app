package com.traderapp.modules.plan.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import com.traderapp.modules.plan.application.events.TradingPlanCreatedEvent;
import com.traderapp.modules.plan.application.ports.output.PlanEventPublisher;
import com.traderapp.modules.plan.domain.entities.TradingPlan;
import com.traderapp.modules.plan.domain.repositories.TradingPlanRepository;
import com.traderapp.modules.plan.domain.valueObjects.TradingPlanId;


@Service
public class CreateTradingPlan {
    private final TradingPlanRepository tradingPlanRepository;
    private final PlanEventPublisher planEventPublisher;

    public CreateTradingPlan (TradingPlanRepository tradingPlanRepository, PlanEventPublisher planEventPublisher) {
        this.tradingPlanRepository = tradingPlanRepository;
        this.planEventPublisher = planEventPublisher;
    }

    public void execute(UUID userId) {
        boolean alreadyExists = tradingPlanRepository.findByUserId(userId).isPresent();

        if(alreadyExists) {
            return;
        }

        TradingPlan tradingPlan = new TradingPlan(
            TradingPlanId.generate(), 
            userId, 
            List.of(), 
            List.of(), 
            LocalDateTime.now(), 
            LocalDateTime.now()
        );
        
        TradingPlan saved = tradingPlanRepository.save(tradingPlan);
        
        planEventPublisher.publishTradingPlanCreated(
            new TradingPlanCreatedEvent(
                saved.getId().toString(),
                saved.getUserId().toString()
            )
        );
    }
}
