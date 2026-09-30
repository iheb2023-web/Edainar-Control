package com.edinar.control.controller;

import com.edinar.control.dto.reconcile.ReconcileRequest;
import com.edinar.control.dto.reconcile.ReconcileResponse;
import com.edinar.control.service.ReconciliationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards")
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    public ReconciliationController(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    @PostMapping("/{id}/reconcile")
    public ResponseEntity<ReconcileResponse> reconcile(
        @PathVariable Long id,
        @Valid @RequestBody ReconcileRequest request
    ) {
        return ResponseEntity.ok(reconciliationService.reconcile(id, request));
    }
}
