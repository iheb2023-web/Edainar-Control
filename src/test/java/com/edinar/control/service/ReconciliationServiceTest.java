package com.edinar.control.service;

import com.edinar.control.domain.Card;
import com.edinar.control.dto.reconcile.ReconcileRequest;
import com.edinar.control.mapper.CardMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReconciliationServiceTest {
    @Test
    void createsPositiveAdjustmentAndStoresVerifiedBalance() {
        CardMapper cards = mock(CardMapper.class);
        LedgerService ledger = mock(LedgerService.class);
        CurrentUserService currentUser = mock(CurrentUserService.class);
        UUID userId = UUID.randomUUID();
        Card card = new Card(); card.setId(2L); card.setCurrentBalance(new BigDecimal("100.000"));
        when(currentUser.getCurrentUserId()).thenReturn(userId);
        when(cards.findByIdAndUserIdForUpdate(2L, userId)).thenReturn(Optional.of(card));
        var tx = new com.edinar.control.dto.transaction.TransactionResponse(); tx.setId(11L);
        when(ledger.createTransaction(anyLong(), any())).thenReturn(tx);

        ReconcileRequest request = new ReconcileRequest(); request.setRealBalance(new BigDecimal("105.000"));
        var response = new ReconciliationService(cards, ledger, currentUser).reconcile(2L, request);

        assertEquals("POSITIVE", response.getAdjustmentType());
        assertEquals(new BigDecimal("5.000"), response.getDifference());
        verify(cards).updateVerifiedBalance(eq(2L), eq(new BigDecimal("105.000")), any(), eq(new BigDecimal("105.000")), any());
    }
}
