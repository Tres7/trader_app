package com.traderapp.modules.plan.infrastructure.persistence.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.traderapp.modules.plan.domain.entities.TradingPlan;
import com.traderapp.modules.plan.domain.entities.TradingPlanCustomField;
import com.traderapp.modules.plan.domain.entities.TradingPlanSection;
import com.traderapp.modules.plan.domain.enums.SectionKey;
import com.traderapp.modules.plan.domain.repositories.TradingPlanRepository;
import com.traderapp.modules.plan.domain.valueObjects.TradingPlanId;
import com.traderapp.modules.plan.infrastructure.persistence.entities.TradingPlanCustomFieldEntity;
import com.traderapp.modules.plan.infrastructure.persistence.entities.TradingPlanEntity;
import com.traderapp.modules.plan.infrastructure.persistence.entities.TradingPlanSectionEntity;

@Repository
public class JpaTradingPlanRepository implements TradingPlanRepository {
    private final SpringDataTradingPlanRepository springDataTradingPlanRepository;

    public JpaTradingPlanRepository(SpringDataTradingPlanRepository springDataTradingPlanRepository) {
        this.springDataTradingPlanRepository = springDataTradingPlanRepository;
    }

    @Override
    public Optional<TradingPlan> findByUserId(UUID userId) {
        return springDataTradingPlanRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public Optional<TradingPlan> findById(TradingPlanId id) {
        return springDataTradingPlanRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public TradingPlan save(TradingPlan plan) {
        TradingPlanEntity entity = toEntity(plan);
        TradingPlanEntity saved = springDataTradingPlanRepository.save(entity);
        return toDomain(saved);
    }

    private TradingPlanEntity toEntity(TradingPlan plan) {
        TradingPlanEntity entity = new TradingPlanEntity(
                plan.getId().value(),
                plan.getUserId(),
                plan.getCreatedAt(),
                plan.getUpdatedAt()
        );

        plan.getSections().forEach(section ->
                entity.getSections().add(new TradingPlanSectionEntity(
                        section.getId(),
                        entity,
                        section.getSectionKey().name(),
                        section.getContent(),
                        section.getComment(),
                        section.getCreatedAt(),
                        section.getUpdatedAt()
                ))
        );

        plan.getCustomFields().forEach(field ->
                entity.getCustomFields().add(new TradingPlanCustomFieldEntity(
                        field.getId(),
                        entity,
                        field.getFieldName(),
                        field.getFieldValue(),
                        field.getDisplayOrder(),
                        field.getComment(),
                        field.getCreatedAt(),
                        field.getUpdatedAt()
                ))
        );

        return entity;
    }

    private TradingPlan toDomain(TradingPlanEntity entity) {
        var sections = entity.getSections().stream()
                .map(s -> new TradingPlanSection(
                    s.getId(),
                    new TradingPlanId(entity.getId()),
                    SectionKey.valueOf(s.getSectionKey()),
                    s.getContent(),
                    s.getComment(),
                    s.getCreatedAt(),
                    s.getUpdatedAt()
                ))
                .toList();

        var customFields = entity.getCustomFields().stream()
                .map(f -> new TradingPlanCustomField(
                    f.getId(),
                    new TradingPlanId(entity.getId()),
                    f.getFieldName(),
                    f.getFieldValue(),
                    f.getDisplayOrder(),
                    f.getComment(),
                    f.getCreatedAt(),
                    f.getUpdatedAt()
                ))
                .toList();

        return new TradingPlan(
            new TradingPlanId(entity.getId()),
            entity.getUserId(),
            sections,
            customFields,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

}
