package com.traderapp.modules.plan.domain.entities;

import com.traderapp.modules.plan.domain.enums.SectionKey;
import com.traderapp.modules.plan.domain.valueObjects.TradingPlanId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TradingPlanTest {

    private TradingPlan emptyPlan() {
        return new TradingPlan(
            TradingPlanId.generate(), UUID.randomUUID(), List.of(), List.of(),
            LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void addCustomField_withGivenDisplayOrder_storesFieldWithCorrectOrder() {
        // Arrange
        TradingPlan plan = emptyPlan();

        // Act
        plan.addCustomField("Risk ratio", "1:2", 1, null);
        plan.addCustomField("Max drawdown", "5%", 2, null);

        // Assert
        List<TradingPlanCustomField> fields = plan.getCustomFields();
        assertThat(fields).hasSize(2);
        assertThat(fields.get(0).getFieldName()).isEqualTo("Risk ratio");
        assertThat(fields.get(0).getDisplayOrder()).isEqualTo(1);
        assertThat(fields.get(1).getDisplayOrder()).isEqualTo(2);
    }

    @Test
    void updateCustomFieldComment_whenFieldExists_updatesCommentOnCorrectField() {
        // Arrange
        TradingPlan plan = emptyPlan();
        plan.addCustomField("Risk ratio", "1:2", 1, null);
        UUID fieldId = plan.getCustomFields().get(0).getId();

        // Act
        plan.updateCustomFieldComment(fieldId, "Updated comment");

        // Assert
        assertThat(plan.getCustomFields().get(0).getComment()).isEqualTo("Updated comment");
    }

    @Test
    void updateSection_whenSectionDoesNotExist_createsSectionWithContent() {
        // Arrange
        TradingPlan plan = emptyPlan();

        // Act
        plan.updateSection(SectionKey.STYLE_TRADING, "Trend following");

        // Assert
        assertThat(plan.getSections()).hasSize(1);
        assertThat(plan.getSections().get(0).getContent()).isEqualTo("Trend following");
    }

    @Test
    void updateSection_whenSectionAlreadyExists_updatesContent() {
        // Arrange
        TradingPlan plan = emptyPlan();
        plan.updateSection(SectionKey.STYLE_TRADING, "First version");

        // Act
        plan.updateSection(SectionKey.STYLE_TRADING, "Updated version");

        // Assert
        assertThat(plan.getSections()).hasSize(1);
        assertThat(plan.getSections().get(0).getContent()).isEqualTo("Updated version");
    }
}
