package com.edinar.control.controller;

import com.edinar.control.dto.analytics.AnalyticsResponse;
import com.edinar.control.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/profit")
    public ResponseEntity<AnalyticsResponse> profit(@RequestParam OffsetDateTime from, @RequestParam OffsetDateTime to) {
        return ResponseEntity.ok(analyticsService.profit(from, to));
    }

    @GetMapping("/expenses")
    public ResponseEntity<AnalyticsResponse> expenses(@RequestParam OffsetDateTime from, @RequestParam OffsetDateTime to) {
        return ResponseEntity.ok(analyticsService.expenses(from, to));
    }

    @GetMapping("/cards")
    public ResponseEntity<AnalyticsResponse> cards(@RequestParam OffsetDateTime from, @RequestParam OffsetDateTime to) {
        return ResponseEntity.ok(analyticsService.cards(from, to));
    }

    @GetMapping("/volume")
    public ResponseEntity<AnalyticsResponse> volume(@RequestParam OffsetDateTime from, @RequestParam OffsetDateTime to) {
        return ResponseEntity.ok(analyticsService.volume(from, to));
    }
}
