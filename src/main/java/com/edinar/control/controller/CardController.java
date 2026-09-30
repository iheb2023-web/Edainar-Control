package com.edinar.control.controller;

import com.edinar.control.dto.card.CardResponse;
import com.edinar.control.dto.card.CreateCardRequest;
import com.edinar.control.dto.card.UpdateCardRequest;
import com.edinar.control.dto.card.UpdateCardStatusRequest;
import com.edinar.control.dto.limit.CardLimitsResponse;
import com.edinar.control.dto.limit.CreateLimitRuleRequest;
import com.edinar.control.dto.transaction.CreateTransactionRequest;
import com.edinar.control.dto.transaction.TransactionPageResponse;
import com.edinar.control.dto.transaction.TransactionResponse;
import com.edinar.control.dto.transaction.VoidTransactionRequest;
import com.edinar.control.dto.transaction.VoidTransactionsPeriodRequest;
import com.edinar.control.exception.BadRequestException;
import com.edinar.control.service.CardService;
import com.edinar.control.service.LedgerService;
import com.edinar.control.service.LimitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;
    private final LedgerService ledgerService;
    private final LimitService limitService;

    public CardController(
        CardService cardService,
        LedgerService ledgerService,
        LimitService limitService
    ) {
        this.cardService = cardService;
        this.ledgerService = ledgerService;
        this.limitService = limitService;
    }

    // =========================================================================
    // Card CRUD
    // =========================================================================

    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CreateCardRequest request) {
        CardResponse response = cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> getAllCards() {
        List<CardResponse> cards = cardService.getAllCards();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable Long id) {
        CardResponse card = cardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardResponse> updateCard(
        @PathVariable Long id,
        @Valid @RequestBody UpdateCardRequest request
    ) {
        CardResponse updated = cardService.updateCard(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CardResponse> updateCardStatus(
        @PathVariable Long id,
        @Valid @RequestBody UpdateCardStatusRequest request
    ) {
        CardResponse updated = cardService.updateCardStatus(id, request);
        return ResponseEntity.ok(updated);
    }

    // =========================================================================
    // Étape 7 — Ledger Transactions
    // =========================================================================

    @PostMapping("/{id}/transactions")
    public ResponseEntity<TransactionResponse> createTransaction(
        @PathVariable Long id,
        @Valid @RequestBody CreateTransactionRequest request
    ) {
        TransactionResponse response = ledgerService.createTransaction(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponse>> getCardTransactions(
        @PathVariable Long id,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) java.time.OffsetDateTime from,
        @RequestParam(required = false) java.time.OffsetDateTime to,
        @RequestParam(required = false) Boolean isVoid,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new BadRequestException("page doit être >= 0 et size compris entre 1 et 100");
        }
        List<TransactionResponse> transactions = ledgerService.getTransactionsByCardPaginated(
            id, type == null ? null : type.toUpperCase(), from, to, isVoid, page, size);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}/transactions/history")
    public ResponseEntity<TransactionPageResponse> getCardTransactionHistory(
        @PathVariable Long id,
        @RequestParam(required = false) java.time.OffsetDateTime from,
        @RequestParam(required = false) java.time.OffsetDateTime to,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new BadRequestException("page doit être >= 0 et size compris entre 1 et 100");
        }
        return ResponseEntity.ok(ledgerService.getTransactionPage(id, from, to, page, size));
    }

    @PostMapping("/{id}/transactions/{transactionId}/void")
    public ResponseEntity<TransactionResponse> voidTransaction(
        @PathVariable Long id,
        @PathVariable Long transactionId,
        @Valid @RequestBody VoidTransactionRequest request
    ) {
        TransactionResponse transaction = ledgerService.getTransactionById(transactionId);
        if (!id.equals(transaction.getCardId())) {
            throw new com.edinar.control.exception.ResourceNotFoundException("Transaction introuvable");
        }
        return ResponseEntity.ok(ledgerService.voidTransaction(transactionId, request));
    }

    @PostMapping("/{id}/transactions/void-period")
    public ResponseEntity<java.util.Map<String, Integer>> voidTransactionsByPeriod(
        @PathVariable Long id,
        @Valid @RequestBody VoidTransactionsPeriodRequest request
    ) {
        int count = ledgerService.voidTransactionsByPeriod(id, request);
        return ResponseEntity.ok(java.util.Map.of("voidedCount", count));
    }

    @PostMapping("/{id}/transactions/history/delete")
    public ResponseEntity<java.util.Map<String, Integer>> deleteTransactionsByPeriod(
        @PathVariable Long id,
        @Valid @RequestBody VoidTransactionsPeriodRequest request
    ) {
        int count = ledgerService.deleteTransactionsByPeriod(id, request);
        return ResponseEntity.ok(java.util.Map.of("deletedCount", count));
    }

    // =========================================================================
    // Étape 8 — Plafonds / Limites
    // =========================================================================

    @GetMapping("/{id}/limits")
    public ResponseEntity<CardLimitsResponse> getCardLimits(@PathVariable Long id) {
        CardLimitsResponse limits = limitService.getCardLimits(id);
        return ResponseEntity.ok(limits);
    }

    @PostMapping("/{id}/limits")
    public ResponseEntity<Void> createCardLimit(
        @PathVariable Long id,
        @Valid @RequestBody CreateLimitRuleRequest request
    ) {
        limitService.createLimitRule(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}/limits/{operationType}/{periodType}")
    public ResponseEntity<Void> updateCardLimit(
        @PathVariable Long id,
        @PathVariable String operationType,
        @PathVariable String periodType,
        @Valid @RequestBody CreateLimitRuleRequest request
    ) {
        limitService.updateLimitRule(id, operationType, periodType, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/limits/reset")
    public ResponseEntity<Void> resetCardLimits(@PathVariable Long id) {
        limitService.resetLimitRules(id);
        return ResponseEntity.noContent().build();
    }
}
