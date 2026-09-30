package com.edinar.control.controller;

import com.edinar.control.dto.card.CardSummaryResponse;
import com.edinar.control.dto.dashboard.DashboardSummaryResponse;
import com.edinar.control.service.CardSummaryService;
import com.edinar.control.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardService dashboardService;
    private final CardSummaryService cardSummaryService;

    public DashboardController(DashboardService dashboardService, CardSummaryService cardSummaryService) {
        this.dashboardService = dashboardService;
        this.cardSummaryService = cardSummaryService;
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardSummaryResponse> dashboardSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @GetMapping("/cards/{id}/summary")
    public ResponseEntity<CardSummaryResponse> cardSummary(@PathVariable Long id) {
        return ResponseEntity.ok(cardSummaryService.getSummary(id));
    }
}
