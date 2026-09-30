package com.edinar.control.service;

import com.edinar.control.domain.LimitRule;
import com.edinar.control.mapper.CardMapper;
import com.edinar.control.mapper.LedgerTransactionMapper;
import com.edinar.control.mapper.LimitRuleMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LimitServiceTest {
    @Test
    void rejectsProjectedDailyAmountAboveLimit() {
        LimitRuleMapper rules = mock(LimitRuleMapper.class);
        LedgerTransactionMapper ledger = mock(LedgerTransactionMapper.class);
        CardMapper cards = mock(CardMapper.class);
        CurrentUserService currentUser = mock(CurrentUserService.class);
        var userId = java.util.UUID.randomUUID();
        when(currentUser.getCurrentUserId()).thenReturn(userId);
        when(cards.findByIdAndUserId(anyLong(), eq(userId))).thenReturn(java.util.Optional.of(new com.edinar.control.domain.Card()));
        LimitRule rule = new LimitRule(); rule.setOperationType("SEND"); rule.setPeriodType("DAILY"); rule.setLimitAmount(new BigDecimal("100"));
        when(rules.findActiveByCardId(anyLong())).thenReturn(List.of(rule));
        when(ledger.sumAmountByCardAndDirectionBetween(anyLong(), eq("OUT"), any(OffsetDateTime.class), any(OffsetDateTime.class)))
            .thenReturn(new BigDecimal("80"));

        LimitService service = new LimitService(rules, ledger, cards, currentUser);
        assertThrows(com.edinar.control.exception.BadRequestException.class, () ->
            service.validateTransactionLimits(1L, "SEND", new BigDecimal("21"), OffsetDateTime.now()));
    }
}
