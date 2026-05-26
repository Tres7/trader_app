package com.traderapp.modules.plan.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.traderapp.modules.plan.domain.enums.SectionKey;
import com.traderapp.modules.plan.domain.valueObjects.TradingPlanId;

public class TradingPlan {
    private final TradingPlanId id;
    private final UUID userId;
    private final List<TradingPlanSection> sections;
    private final List<TradingPlanCustomField> customFields;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TradingPlan(
        TradingPlanId id,
        UUID userId,
        List<TradingPlanSection> sections,
        List<TradingPlanCustomField> customFields,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "TradingPlan id cannot be null");
        this.userId = Objects.requireNonNull(userId, "User id cannot be null");
        this.sections = new ArrayList<>(sections != null ? sections : List.of());
        this.customFields = new ArrayList<>(customFields != null ? customFields : List.of());
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt cannot be null");
    }

    public void updateSection(SectionKey key, String content) {
        findSection(key).ifPresentOrElse(
            section -> section.updateContent(content),
            () -> sections.add(new TradingPlanSection(
                UUID.randomUUID(), this.id, key, content, null,
                LocalDateTime.now(), LocalDateTime.now()
            ))
        );
        touch();
    }

    public void updateSectionComment(SectionKey key, String comment) {
        findSection(key).ifPresent(section -> section.updateComment(comment));
        touch();
    }

    public void addCustomField(String fieldName, String fieldValue, int displayOrder, String comment) {
        customFields.add(new TradingPlanCustomField(
            UUID.randomUUID(), this.id, fieldName, fieldValue, displayOrder, comment,
            LocalDateTime.now(), LocalDateTime.now()
        ));
        touch();
    }

    public void updateCustomField(UUID customFieldId, String fieldName, String fieldValue, int displayOrder) {
        findCustomField(customFieldId).ifPresent(f -> f.update(fieldName, fieldValue, displayOrder));
        touch();
    }

    public void updateCustomFieldComment(UUID customFieldId, String comment) {
        findCustomField(customFieldId).ifPresent(f -> f.updateComment(comment));
        touch();
    }

    public void removeCustomField(UUID customFieldId) {
        customFields.removeIf(f -> f.getId().equals(customFieldId));
        touch();
    }

    private Optional<TradingPlanSection> findSection(SectionKey key) {
        return sections.stream().filter(s -> s.getSectionKey() == key).findFirst();
    }

    private Optional<TradingPlanCustomField> findCustomField(UUID id) {
        return customFields.stream().filter(f -> f.getId().equals(id)).findFirst();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public TradingPlanId getId() { 
        return id; 
    }

    public UUID getUserId() { 
        return userId; 
    }

    public List<TradingPlanSection> getSections() { 
        return Collections.unmodifiableList(sections); 
    }

    public List<TradingPlanCustomField> getCustomFields() { 
        return Collections.unmodifiableList(customFields); 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }

    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }
}
