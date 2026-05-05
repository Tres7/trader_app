package com.traderapp.modules.plan.application.usecases;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.traderapp.modules.plan.application.commands.UpdateTradingPlanCommand;
import com.traderapp.modules.plan.application.events.TradingPlanUpdatedEvent;
import com.traderapp.modules.plan.application.ports.output.PlanEventPublisher;
import com.traderapp.modules.plan.domain.entities.TradingPlan;
import com.traderapp.modules.plan.domain.repositories.TradingPlanRepository;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UpdateTradingPlan {
    private final TradingPlanRepository tradingPlanRepository;

    private final PlanEventPublisher planEventPublisher;

    public UpdateTradingPlan(TradingPlanRepository tradingPlanRepository, PlanEventPublisher planEventPublisher) {
        this.tradingPlanRepository = tradingPlanRepository;
        this.planEventPublisher = planEventPublisher;
    }
    
    @Transactional
    public TradingPlan execute(UUID userId, UpdateTradingPlanCommand command) {
        TradingPlan plan = tradingPlanRepository.findByUserId(userId).orElseThrow(() -> new IllegalStateException("Trading plan not found for user " + userId));

        command.sections().forEach(s -> plan.updateSection(s.key(), s.content()));

        command.sections().forEach(s -> {
            if (s.comment() != null) {
                plan.updateSectionComment(s.key(), s.comment());
            }
        });

        // get des existing customfields, delete them and save 
        List<UUID> existingIds = plan.getCustomFields().stream()
                    .map(f -> f.getId())
                    .toList();

        existingIds.forEach(plan::removeCustomField);

        command.customFields().forEach(f -> plan.addCustomField(f.fieldName(), f.fieldValue(), f.displayOrder()));

        TradingPlan saved = tradingPlanRepository.save(plan);
        
        planEventPublisher.publishTradingPlanUpdated(
            new TradingPlanUpdatedEvent(
                saved.getId().toString(),
                saved.getUserId().toString()
            )
        );

        return saved; 
    }
}
