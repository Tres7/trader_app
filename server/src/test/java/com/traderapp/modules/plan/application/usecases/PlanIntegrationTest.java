package com.traderapp.modules.plan.application.usecases;

import com.traderapp.BaseIntegrationTest;
import com.traderapp.modules.auth.application.commands.RegisterUserCommand;
import com.traderapp.modules.auth.application.usecases.RegisterUser;
import com.traderapp.modules.auth.domain.entities.User;
import com.traderapp.modules.plan.application.commands.UpdateTradingPlanCommand;
import com.traderapp.modules.plan.domain.entities.TradingPlan;
import com.traderapp.modules.plan.domain.entities.TradingPlanCustomField;
import com.traderapp.modules.plan.domain.enums.SectionKey;
import com.traderapp.modules.plan.domain.repositories.TradingPlanRepository;
import com.traderapp.modules.plan.infrastructure.messaging.RabbitMqPlanConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;


import static org.assertj.core.api.Assertions.assertThat;

class PlanIntegrationTest extends BaseIntegrationTest {

    @Autowired 
    RegisterUser registerUser;

    @Autowired 
    CreateTradingPlan createTradingPlan;

    @Autowired 
    UpdateTradingPlan updateTradingPlan;

    @Autowired 
    TradingPlanRepository tradingPlanRepository;

    @Autowired 
    RabbitTemplate rabbitTemplate;

    @Autowired 
    JdbcTemplate jdbcTemplate;

    @Autowired
    AmqpAdmin amqpAdmin;

    private User testUser;

    @BeforeEach
    void setup() {
        jdbcTemplate.execute("TRUNCATE TABLE trading_plan_custom_field, trading_plan_section, trading_plan, email_verification_codes, users CASCADE");
        testUser = registerUser.execute(new RegisterUserCommand(
            "Alice", "Test", LocalDate.of(1995, 6, 15),
            "alice@example.com", "Password123!", "FR"
        ));
    }

    @Test
    void createTradingPlan_persistsPlanToDatabase() {
        // Act
        createTradingPlan.execute(testUser.getId().value());

        // Assert
        Optional<TradingPlan> found = tradingPlanRepository.findByUserId(testUser.getId().value());
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(testUser.getId().value());
    }

    @Test
    void updateTradingPlan_withCustomField_persistsWithCorrectDisplayOrder() {
        // Arrange
        createTradingPlan.execute(testUser.getId().value());
        UpdateTradingPlanCommand command = new UpdateTradingPlanCommand(
            List.of(),
            List.of(
                new UpdateTradingPlanCommand.CustomFieldCommand("Risk ratio", "1:2", 1, null),
                new UpdateTradingPlanCommand.CustomFieldCommand("Max drawdown", "5%", 2, null)
            )
        );

        // Act
        updateTradingPlan.execute(testUser.getId().value(), command);

        // Assert
        TradingPlan saved = tradingPlanRepository.findByUserId(testUser.getId().value()).get();
        List<TradingPlanCustomField> fields = saved.getCustomFields();
        assertThat(fields).hasSize(2);
        assertThat(fields).extracting(TradingPlanCustomField::getDisplayOrder).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    void updateTradingPlan_withCustomFieldComment_persistsCommentToCorrectField() {
        // Arrange
        createTradingPlan.execute(testUser.getId().value());
        UpdateTradingPlanCommand command = new UpdateTradingPlanCommand(
            List.of(),
            List.of(new UpdateTradingPlanCommand.CustomFieldCommand("Risk ratio", "1:2", 1, "My comment"))
        );

        // Act
        updateTradingPlan.execute(testUser.getId().value(), command);

        // Assert
        TradingPlan saved = tradingPlanRepository.findByUserId(testUser.getId().value()).get();
        assertThat(saved.getCustomFields().get(0).getComment()).isEqualTo("My comment");
        assertThat(saved.getCustomFields().get(0).getFieldName()).isEqualTo("Risk ratio");
    }



    @Test
    void updateTradingPlan_publishesEventToRabbitMQ() {
        // Arrange — temporary queue test binded to the same routing key
        String testQueue = "test.plan-updated.queue";
        amqpAdmin.declareQueue(new Queue(testQueue, false, false, true));
        amqpAdmin.declareBinding(new Binding(
            testQueue,
            Binding.DestinationType.QUEUE,
            RabbitMqPlanConfig.PLAN_EXCHANGE,
            RabbitMqPlanConfig.PLAN_UPDATED_ROUTING_KEY,
            null
        ));

        createTradingPlan.execute(testUser.getId().value());
        UpdateTradingPlanCommand command = new UpdateTradingPlanCommand(
            List.of(new UpdateTradingPlanCommand.SectionCommand(SectionKey.STYLE_TRADING, "Trend following", null)),
            List.of()
        );

        // Act
        updateTradingPlan.execute(testUser.getId().value(), command);

        // Assert
        Message message = rabbitTemplate.receive(testQueue, 3000);
        assertThat(message).isNotNull();

        amqpAdmin.deleteQueue(testQueue);
    }

}
