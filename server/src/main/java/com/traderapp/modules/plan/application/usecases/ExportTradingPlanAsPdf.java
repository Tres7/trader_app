package com.traderapp.modules.plan.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.traderapp.modules.plan.application.events.TradingPlanExportedEvent;
import com.traderapp.modules.plan.application.ports.output.PlanEventPublisher;
import com.traderapp.modules.plan.application.ports.output.PlanExportPort;
import com.traderapp.modules.plan.domain.entities.TradingPlan;
import com.traderapp.modules.plan.domain.repositories.TradingPlanRepository;

@Service
public class ExportTradingPlanAsPdf {
    private final TradingPlanRepository tradingPlanRepository;
    private final PlanExportPort planExportPort;
    private final PlanEventPublisher planEventPublisher;

    public ExportTradingPlanAsPdf(TradingPlanRepository tradingPlanRepository, PlanExportPort planExportPort,PlanEventPublisher planEventPublisher) {
        this.tradingPlanRepository = tradingPlanRepository;
        this.planExportPort = planExportPort;
        this.planEventPublisher = planEventPublisher;
    }

    public byte[] execute(UUID userId) {
        TradingPlan plan = tradingPlanRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Trading plan not found for user " + userId));
        
        byte[] pdf = planExportPort.exportAsPdf(plan);

        planEventPublisher.publishTradingPlanExported(
            new TradingPlanExportedEvent(plan.getId().value().toString(), userId.toString())
        );
        return pdf;
    }
}
