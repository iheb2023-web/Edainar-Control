package com.edinar.control.service;

import com.edinar.control.domain.Card;
import com.edinar.control.dto.reconcile.ReconcileRequest;
import com.edinar.control.dto.reconcile.ReconcileResponse;
import com.edinar.control.dto.transaction.CreateTransactionRequest;
import com.edinar.control.dto.transaction.TransactionResponse;
import com.edinar.control.exception.BadRequestException;
import com.edinar.control.exception.ResourceNotFoundException;
import com.edinar.control.mapper.CardMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ReconciliationService {

    private final CardMapper cardMapper;
    private final LedgerService ledgerService;
    private final CurrentUserService currentUserService;

    public ReconciliationService(
        CardMapper cardMapper,
        LedgerService ledgerService,
        CurrentUserService currentUserService
    ) {
        this.cardMapper = cardMapper;
        this.ledgerService = ledgerService;
        this.currentUserService = currentUserService;
    }

    @Transactional(rollbackFor = Exception.class)
    public ReconcileResponse reconcile(Long cardId, ReconcileRequest request) {
        if (request.getRealBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Le solde réel ne peut pas être négatif");
        }

        UUID userId = currentUserService.getCurrentUserId();
        Card card = cardMapper.findByIdAndUserIdForUpdate(cardId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Carte introuvable ou accès non autorisé : " + cardId));

        BigDecimal calculated = card.getCurrentBalance();
        BigDecimal difference = request.getRealBalance().subtract(calculated);
        OffsetDateTime reconciledAt = OffsetDateTime.now();
        TransactionResponse adjustment = null;

        if (difference.signum() != 0) {
            CreateTransactionRequest adjustmentRequest = new CreateTransactionRequest();
            adjustmentRequest.setType("ADJUSTMENT");
            adjustmentRequest.setDirection(difference.signum() > 0 ? "IN" : "OUT");
            adjustmentRequest.setAmount(difference.abs());
            adjustmentRequest.setFeeAmount(BigDecimal.ZERO);
            adjustmentRequest.setDescription(request.getReason() != null
                ? request.getReason() : "Réconciliation du solde");
            adjustmentRequest.setReference("RECONCILE-" + cardId);
            adjustment = ledgerService.createTransaction(cardId, adjustmentRequest);
        }

        cardMapper.updateVerifiedBalance(
            cardId, request.getRealBalance(), reconciledAt, request.getRealBalance(), reconciledAt);

        ReconcileResponse response = new ReconcileResponse();
        response.setCardId(cardId);
        response.setCalculatedBalance(calculated);
        response.setRealBalance(request.getRealBalance());
        response.setDifference(difference);
        response.setAdjustmentType(difference.signum() == 0 ? "NONE" : difference.signum() > 0 ? "POSITIVE" : "NEGATIVE");
        response.setAdjustmentTransactionId(adjustment == null ? null : adjustment.getId());
        response.setReconciledAt(reconciledAt);
        return response;
    }
}
