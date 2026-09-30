package com.edinar.control.controller;

import com.edinar.control.dto.p2p.CreateFeeRuleRequest;
import com.edinar.control.dto.p2p.FeeRuleResponse;
import com.edinar.control.service.FeeRuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fee-rules")
public class FeeRuleController {

    private final FeeRuleService feeRuleService;

    public FeeRuleController(FeeRuleService feeRuleService) {
        this.feeRuleService = feeRuleService;
    }

    /**
     * GET /api/fee-rules
     * Lister toutes les règles de frais (historique complet, immuable).
     */
    @GetMapping
    public ResponseEntity<List<FeeRuleResponse>> getAllRules() {
        return ResponseEntity.ok(feeRuleService.getAllRules());
    }

    /**
     * POST /api/fee-rules
     * Créer une NOUVELLE règle de frais.
     * IMPORTANT : on ne modifie jamais une règle existante (Étape 10 — immuabilité).
     * Chaque changement tarifaire = nouvelle règle avec effective_from.
     */
    @PostMapping
    public ResponseEntity<FeeRuleResponse> createRule(@Valid @RequestBody CreateFeeRuleRequest request) {
        FeeRuleResponse response = feeRuleService.createRule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
