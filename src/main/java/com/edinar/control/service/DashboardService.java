package com.edinar.control.service;

import com.edinar.control.domain.Card;
import com.edinar.control.domain.LedgerTransaction;
import com.edinar.control.dto.dashboard.DashboardSummaryResponse;
import com.edinar.control.mapper.CardMapper;
import com.edinar.control.mapper.LedgerTransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Service
public class DashboardService {

    private final CardMapper cardMapper;
    private final LedgerTransactionMapper ledgerTransactionMapper;
    private final LedgerService ledgerService;
    private final CurrentUserService currentUserService;

    public DashboardService(
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
    public DashboardSummaryResponse getSummary() {
        List<Card> cards = cardMapper.findByUserId(currentUserService.getCurrentUserId());
        List<LedgerTransaction> transactions = cards.stream()
            .flatMap(card -> ledgerTransactionMapper.findRecentByCardId(card.getId(), 20).stream())
            .filter(transaction -> !Boolean.TRUE.equals(transaction.getIsVoid()))
            .sorted(Comparator.comparing(LedgerTransaction::getTransactionDate).reversed())
            .toList();

        DashboardSummaryResponse response = new DashboardSummaryResponse();
        response.setTotalCards(cards.size());
        response.setActiveCards(cards.stream().filter(card -> "ACTIVE".equalsIgnoreCase(card.getStatus())).count());
        response.setTotalBalance(cards.stream().map(Card::getCurrentBalance).reduce(BigDecimal.ZERO, BigDecimal::add));
        response.setTotalIn(sum(transactions, "IN"));
        response.setTotalOut(sum(transactions, "OUT"));
        ZoneId timezone = ZoneId.of("Africa/Tunis");
        OffsetDateTime todayStart = java.time.LocalDate.now(timezone).atStartOfDay(timezone).toOffsetDateTime();
        OffsetDateTime todayEnd = todayStart.plusDays(1).minusNanos(1);
        response.setTodayIn(sumForDay(cards, "IN", todayStart, todayEnd));
        response.setTodayOut(sumForDay(cards, "OUT", todayStart, todayEnd));
        response.setRecentTransactions(transactions.stream().limit(20).map(ledgerService::toResponse).toList());
        return response;
    }

    private BigDecimal sumForDay(List<Card> cards, String direction, OffsetDateTime start, OffsetDateTime end) {
        return cards.stream()
            .map(card -> ledgerTransactionMapper.sumAmountAndFeesByCardAndDirectionBetween(card.getId(), direction, start, end))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
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
