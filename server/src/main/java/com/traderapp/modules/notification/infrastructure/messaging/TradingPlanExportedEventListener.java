package com.traderapp.modules.notification.infrastructure.messaging;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.traderapp.modules.notification.application.usecases.SendInAppNotification;
import com.traderapp.modules.plan.application.events.TradingPlanExportedEvent;

@Component
public class TradingPlanExportedEventListener {
    private static final Logger log = LoggerFactory.getLogger(TradingPlanExportedEventListener.class);

    private final SendInAppNotification sendInAppNotification;

    public TradingPlanExportedEventListener(SendInAppNotification sendInAppNotification) {
        this.sendInAppNotification = sendInAppNotification;
    }

    @RabbitListener(queues = RabbitMqNotificationConfig.TRADING_PLAN_EXPORTED_QUEUE)
    public void handle(TradingPlanExportedEvent event) {
        log.info("TradingPlanExportedEvent received for userId: {}", event.userId());
        sendInAppNotification.execute(UUID.fromString(event.userId()),
            "TRADING_PLAN_EXPORTED",
            "Export réussi",
            "Votre plan de trading a bien été exporté en PDF."
        );
    }

}
