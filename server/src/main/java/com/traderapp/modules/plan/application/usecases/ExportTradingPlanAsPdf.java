package com.traderapp.modules.plan.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.traderapp.modules.plan.application.ports.output.PlanExportPort;
import com.traderapp.modules.plan.domain.entities.TradingPlan;
import com.traderapp.modules.plan.domain.repositories.TradingPlanRepository;

@Service
public class ExportTradingPlanAsPdf {
    private final TradingPlanRepository tradingPlanRepository;
    private final PlanExportPort planExportPort;

    public ExportTradingPlanAsPdf(TradingPlanRepository tradingPlanRepository, PlanExportPort planExportPort) {
        this.tradingPlanRepository = tradingPlanRepository;
        this.planExportPort = planExportPort;
    }

    public byte[] execute(UUID userId) {
        TradingPlan plan = tradingPlanRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Trading plan not found for user " + userId));
        return planExportPort.exportAsPdf(plan);
    }
}
