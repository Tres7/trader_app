package com.traderapp.modules.plan.application.usecases;

import com.traderapp.modules.plan.application.events.TradingPlanCreatedEvent;
import com.traderapp.modules.plan.application.ports.output.PlanEventPublisher;
import com.traderapp.modules.plan.domain.entities.TradingPlan;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTradingPlanTest {

    @Mock TradingPlanRepository tradingPlanRepository;
    @Mock PlanEventPublisher planEventPublisher;

    @InjectMocks CreateTradingPlan createTradingPlan;

    @Test
    void execute_whenNoPlanExists_savesAndPublishesEvent() {
        // Arrange
        UUID userId = UUID.randomUUID();
        TradingPlan saved = new TradingPlan(
            TradingPlanId.generate(), userId, List.of(), List.of(),
            LocalDateTime.now(), LocalDateTime.now()
        );
        when(tradingPlanRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(tradingPlanRepository.save(any(TradingPlan.class))).thenReturn(saved);

        // Act
        createTradingPlan.execute(userId);

        // Assert
        verify(tradingPlanRepository).save(any(TradingPlan.class));
        verify(planEventPublisher).publishTradingPlanCreated(any(TradingPlanCreatedEvent.class));
    }

    @Test
    void execute_whenPlanAlreadyExists_doesNothing() {
        // Arrange
        UUID userId = UUID.randomUUID();
        TradingPlan existing = new TradingPlan(
            TradingPlanId.generate(), userId, List.of(), List.of(),
            LocalDateTime.now(), LocalDateTime.now()
        );
        when(tradingPlanRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        // Act
        createTradingPlan.execute(userId);

        // Assert
        verify(tradingPlanRepository, never()).save(any());
        verify(planEventPublisher, never()).publishTradingPlanCreated(any());
    }
}