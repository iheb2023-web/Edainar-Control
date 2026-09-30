package com.edinar.control.service;

import com.edinar.control.domain.Card;
import com.edinar.control.dto.transfer.InternalTransferRequest;
import com.edinar.control.mapper.CardMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class InternalTransferServiceTest {
    @Test
    void rejectsTransferToSameCard() {
        InternalTransferService service = new InternalTransferService(mock(CardMapper.class), mock(LedgerService.class), mock(CurrentUserService.class));
        InternalTransferRequest request = new InternalTransferRequest();
        request.setSourceCardId(1L); request.setDestinationCardId(1L); request.setAmount(BigDecimal.ONE);
        assertThrows(com.edinar.control.exception.BadRequestException.class, () -> service.transfer(request));
    }

    @Test
    void locksCardsInDeterministicIdOrder() {
        CardMapper cards = mock(CardMapper.class); LedgerService ledger = mock(LedgerService.class); CurrentUserService user = mock(CurrentUserService.class);
        UUID userId = UUID.randomUUID(); when(user.getCurrentUserId()).thenReturn(userId);
        Card first = new Card(); first.setStatus("ACTIVE"); Card second = new Card(); second.setStatus("ACTIVE");
        when(cards.findByIdAndUserIdForUpdate(anyLong(), eq(userId))).thenReturn(Optional.of(first));
        when(ledger.createTransactionWithGroup(anyLong(), any(), any())).thenReturn(new com.edinar.control.dto.transaction.TransactionResponse());
        InternalTransferService service = new InternalTransferService(cards, ledger, user);
        InternalTransferRequest request = new InternalTransferRequest(); request.setSourceCardId(10L); request.setDestinationCardId(3L); request.setAmount(BigDecimal.ONE);
        service.transfer(request);
        var order = inOrder(cards);
        order.verify(cards).findByIdAndUserIdForUpdate(eq(3L), eq(userId));
        order.verify(cards).findByIdAndUserIdForUpdate(eq(10L), eq(userId));
    }
}
