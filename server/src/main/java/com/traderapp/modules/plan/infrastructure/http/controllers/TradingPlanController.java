package com.traderapp.modules.plan.infrastructure.http.controllers;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import java.util.UUID;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.traderapp.modules.auth.application.dto.AuthenticatedUser;
import com.traderapp.modules.plan.application.commands.UpdateTradingPlanCommand;
import com.traderapp.modules.plan.application.usecases.ExportTradingPlanAsPdf;
import com.traderapp.modules.plan.application.usecases.GetTradingPlan;
import com.traderapp.modules.plan.application.usecases.UpdateTradingPlan;
import com.traderapp.modules.plan.infrastructure.http.responses.TradingPlanResponse;
import com.traderapp.modules.plan.presentation.rest.requests.UpdateTradingPlanRequest;

@RestController
@RequestMapping("/api/v1/plan")
public class TradingPlanController {
    private final GetTradingPlan getTradingPlan;
    private final UpdateTradingPlan updateTradingPlan;
    private final ExportTradingPlanAsPdf exportTradingPlanAsPdf;

    public TradingPlanController(GetTradingPlan getTradingPlan, UpdateTradingPlan updateTradingPlan, ExportTradingPlanAsPdf exportTradingPlanAsPdf) {
        this.getTradingPlan = getTradingPlan;
        this.updateTradingPlan = updateTradingPlan;
        this.exportTradingPlanAsPdf = exportTradingPlanAsPdf;
    }

    @GetMapping
    public ResponseEntity<TradingPlanResponse> getPlan(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        UUID userId = UUID.fromString(authenticatedUser.userId());
        return getTradingPlan.execute(userId)
            .map(TradingPlanResponse::from)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<TradingPlanResponse> updatePlan(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @RequestBody UpdateTradingPlanRequest request
    ) {
        UUID userId = UUID.fromString(authenticatedUser.userId());

        UpdateTradingPlanCommand command = new UpdateTradingPlanCommand(
            request.sections().stream()
                .map(s -> new UpdateTradingPlanCommand.SectionCommand(s.key(), s.content(), s.comment()))
                .toList(),
            request.customFields().stream()
                .map(f -> new UpdateTradingPlanCommand.CustomFieldCommand(f.fieldName(), f.fieldValue(), f.displayOrder(), f.comment()))
                .toList()
        );

        TradingPlanResponse response = TradingPlanResponse.from(updateTradingPlan.execute(userId, command));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
        @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
        @RequestParam String format
    ) {
        UUID userId = UUID.fromString(authenticatedUser.userId());

        return switch (format.toUpperCase()) {
            case "PDF" -> {
                byte[] pdf = exportTradingPlanAsPdf.execute(userId);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDisposition(ContentDisposition.attachment().filename("trading-plan.pdf").build());
                yield ResponseEntity.ok().headers(headers).body(pdf);
            }
            default -> ResponseEntity.badRequest().build();
        };
    }

}
