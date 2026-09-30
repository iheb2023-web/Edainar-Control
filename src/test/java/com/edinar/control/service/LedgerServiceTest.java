package com.edinar.control.service;

import com.edinar.control.domain.Card;
import com.edinar.control.dto.transaction.CreateTransactionRequest;
import com.edinar.control.mapper.CardMapper;
import com.edinar.control.mapper.LedgerTransactionMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LedgerServiceTest {
    @Test
    void createsSendWithFeeAndLocksCard() {
        LedgerTransactionMapper transactions = mock(LedgerTransactionMapper.class);
        CardMapper cards = mock(CardMapper.class);
        LimitService limits = mock(LimitService.class);
        AuditLogService audit = mock(AuditLogService.class);
        CurrentUserService currentUser = mock(CurrentUserService.class);
        Card card = new Card();
        card.setId(1L); card.setUserId(java.util.UUID.randomUUID()); card.setStatus("ACTIVE");
        card.setCurrentBalance(new BigDecimal("100.000"));
        when(currentUser.getCurrentUserId()).thenReturn(card.getUserId());
        when(cards.findByIdAndUserIdForUpdate(eq(1L), eq(card.getUserId()))).thenReturn(Optional.of(card));
        doAnswer(invocation -> { invocation.getArgument(0, com.edinar.control.domain.LedgerTransaction.class).setId(9L); return 1; })
            .when(transactions).insert(any());

        LedgerService service = new LedgerService(transactions, cards, limits, audit, currentUser);
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setType("SEND"); request.setAmount(new BigDecimal("20.000")); request.setFeeAmount(new BigDecimal("1.500"));
        var response = service.createTransaction(1L, request);

        assertEquals(new BigDecimal("78.500"), response.getBalanceAfter());
        verify(cards).updateBalance(eq(1L), eq(new BigDecimal("78.500")), any());
    }
}
