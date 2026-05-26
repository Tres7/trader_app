package com.traderapp.modules.notification.infrastructure.messaging;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.traderapp.modules.notification.application.usecases.SendInAppNotification;
import com.traderapp.modules.plan.application.events.TradingPlanUpdatedEvent;

@Component
public class TradingPlanUpdatedEventListener {

    private static final Logger log = LoggerFactory.getLogger(TradingPlanUpdatedEventListener.class);

    private final SendInAppNotification sendInAppNotification;

    public TradingPlanUpdatedEventListener(SendInAppNotification sendInAppNotification) {
        this.sendInAppNotification = sendInAppNotification;
    }

    @RabbitListener(queues = RabbitMqNotificationConfig.TRADING_PLAN_UPDATED_QUEUE)
    public void handle(TradingPlanUpdatedEvent event) {
        log.info("TradingPlanUpdatedEvent received for userId: {}", event.userId());
        sendInAppNotification.execute(
            UUID.fromString(event.userId()),
            "TRADING_PLAN_UPDATED",
            "Plan de trading mis à jour",
            "Vos modifications ont bien été enregistrées."
        );
    }
}