package com.traderapp.modules.plan.application.ports.output;

import com.traderapp.modules.plan.domain.entities.TradingPlan;

public interface PlanExportPort {
    byte[] exportAsPdf(TradingPlan plan);
}
