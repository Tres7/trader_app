package com.traderapp.modules.plan.application.usecases;

import com.traderapp.modules.plan.application.commands.UpdateTradingPlanCommand;
import com.traderapp.modules.plan.application.ports.output.PlanEventPublisher;
import com.traderapp.modules.plan.domain.entities.TradingPlan;
import com.traderapp.modules.plan.domain.enums.SectionKey;
import com.traderapp.modules.plan.domain.repositories.TradingPlanRepository;
import com.traderapp.modules.plan.domain.valueObjects.TradingPlanId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateTradingPlanTest {

    @Mock 
    TradingPlanRepository tradingPlanRepository;

    @Mock 
    PlanEventPublisher planEventPublisher;

    @InjectMocks 
    UpdateTradingPlan updateTradingPlan;

    @Test
    void execute_whenPlanExists_savesAndPublishesEvent() {
        // Arrange
        UUID userId = UUID.randomUUID();
        TradingPlan plan = new TradingPlan(
            TradingPlanId.generate(), userId, List.of(), List.of(),
            LocalDateTime.now(), LocalDateTime.now()
        );
        UpdateTradingPlanCommand command = new UpdateTradingPlanCommand(
            List.of(new UpdateTradingPlanCommand.SectionCommand(SectionKey.STYLE_TRADING, "My strategy", null)),
            List.of()
        );
        when(tradingPlanRepository.findByUserId(userId)).thenReturn(Optional.of(plan));
        when(tradingPlanRepository.save(any(TradingPlan.class))).thenReturn(plan);

        // Act
        updateTradingPlan.execute(userId, command);

        // Assert
        verify(tradingPlanRepository).save(plan);
        verify(planEventPublisher).publishTradingPlanUpdated(any());
    }

    @Test
    void execute_whenPlanNotFound_throwsIllegalStateException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UpdateTradingPlanCommand command = new UpdateTradingPlanCommand(List.of(), List.of());
        when(tradingPlanRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> updateTradingPlan.execute(userId, command))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Trading plan not found for user");
    }
}
