package com.traderapp.modules.notification.infrastructure.messaging;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.traderapp.modules.notification.application.usecases.SendInAppNotification;
import com.traderapp.modules.plan.application.events.TradingPlanCreatedEvent;
import com.traderapp.modules.plan.infrastructure.messaging.RabbitMqPlanConfig;

@Component
public class TradingPlanCreatedEventListener {

    private static final Logger log = LoggerFactory.getLogger(TradingPlanCreatedEventListener.class);

    private final SendInAppNotification sendInAppNotification;

    public TradingPlanCreatedEventListener(SendInAppNotification sendInAppNotification) {
        this.sendInAppNotification = sendInAppNotification;
    }

    @RabbitListener(queues = RabbitMqPlanConfig.PLAN_CREATED_NOTIFICATION_QUEUE)
    public void handle(TradingPlanCreatedEvent event) {
        log.info("TradingPlanCreatedEvent received for userId: {}", event.userId());
        sendInAppNotification.execute(
            UUID.fromString(event.userId()),
            "TRADING_PLAN_CREATED",
            "Plan de trading créé",
            "Votre espace de trading est prêt. Commencez à remplir votre plan !"
        );
    }
}