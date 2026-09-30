package com.edinar.control.service;

import com.edinar.control.domain.Card;
import com.edinar.control.domain.LedgerTransaction;
import com.edinar.control.dto.card.CardSummaryResponse;
import com.edinar.control.exception.ResourceNotFoundException;
import com.edinar.control.mapper.CardMapper;
import com.edinar.control.mapper.LedgerTransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CardSummaryService {

    private final CardMapper cardMapper;
    private final LedgerTransactionMapper ledgerTransactionMapper;
    private final LedgerService ledgerService;
    private final CurrentUserService currentUserService;

    public CardSummaryService(
        CardMapper cardMapper,
        LedgerTransactionMapper ledgerTransactionMapper,
        LedgerService ledgerService,
        CurrentUserService currentUserService
    ) {
        this.cardMapper = cardMapper;
        this.ledgerTransactionMapper = ledgerTransactionMapper;
        this.ledgerService = ledgerService;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public CardSummaryResponse getSummary(Long cardId) {
        Card card = cardMapper.findByIdAndUserId(cardId, currentUserService.getCurrentUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Carte introuvable ou accès non autorisé : " + cardId));
        List<LedgerTransaction> transactions = ledgerTransactionMapper.findByCardId(cardId);
        List<LedgerTransaction> active = transactions.stream()
            .filter(transaction -> !Boolean.TRUE.equals(transaction.getIsVoid()))
            .toList();

        CardSummaryResponse response = new CardSummaryResponse();
        response.setCardId(card.getId());
        response.setName(card.getName());
        response.setCurrentBalance(card.getCurrentBalance());
        response.setLastVerifiedBalance(card.getLastVerifiedBalance());
        response.setTransactionCount(active.size());
        response.setTotalIn(sum(active, "IN"));
        response.setTotalOut(sum(active, "OUT"));
        response.setRecentTransactions(ledgerTransactionMapper.findRecentByCardId(cardId, 10).stream()
            .map(ledgerService::toResponse).toList());
        return response;
    }

    private BigDecimal sum(List<LedgerTransaction> transactions, String direction) {
        return transactions.stream()
            .filter(transaction -> direction.equalsIgnoreCase(transaction.getDirection()))
            .map(transaction -> transaction.getAmount().add(
                "OUT".equalsIgnoreCase(direction) && transaction.getFeeAmount() != null
                    ? transaction.getFeeAmount() : BigDecimal.ZERO))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
