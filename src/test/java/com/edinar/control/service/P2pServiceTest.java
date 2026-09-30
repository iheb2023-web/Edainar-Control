package com.edinar.control.service;

import com.edinar.control.domain.P2pOrder;
import com.edinar.control.domain.P2pTrade;
import com.edinar.control.dto.p2p.CreateOrderRequest;
import com.edinar.control.dto.p2p.CreateTradeRequest;
import com.edinar.control.mapper.CardMapper;
import com.edinar.control.mapper.P2pOrderMapper;
import com.edinar.control.mapper.P2pTradeMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class P2pServiceTest {
    @Test
    void storesOrderAmountWithoutProfitCalculations() {
        P2pTradeMapper trades = mock(P2pTradeMapper.class);
        P2pOrderMapper orders = mock(P2pOrderMapper.class);
        CardMapper cards = mock(CardMapper.class);
        FeeRuleService fees = mock(FeeRuleService.class);
        LedgerService ledger = mock(LedgerService.class);
        AuditLogService audit = mock(AuditLogService.class);
        CurrentUserService currentUser = mock(CurrentUserService.class);
        UUID userId = UUID.randomUUID(); when(currentUser.getCurrentUserId()).thenReturn(userId);
        P2pTrade trade = new P2pTrade(); trade.setId(1L); trade.setUserId(userId); trade.setAsset("USDT"); trade.setQuoteCurrency("TND"); trade.setStatus("OPEN");
        trade.setTotalBuyUsdt(BigDecimal.ZERO); trade.setTotalSellUsdt(BigDecimal.ZERO); trade.setTotalBuyTnd(BigDecimal.ZERO); trade.setTotalSellTnd(BigDecimal.ZERO);
        trade.setTotalBinanceFeeUsdt(BigDecimal.ZERO); trade.setTotalBinanceFeeTnd(BigDecimal.ZERO); trade.setTotalOtherFeesTnd(BigDecimal.ZERO);
        when(trades.findById(1L)).thenReturn(Optional.of(trade));
        doAnswer(invocation -> { invocation.getArgument(0, P2pOrder.class).setId(2L); return 1; }).when(orders).insert(any());

        P2pService service = new P2pService(trades, orders, cards, fees, ledger, audit, currentUser);
        CreateTradeRequest create = new CreateTradeRequest();
        service.createTrade(create);
        CreateOrderRequest order = order("BUY", "125.500");
        service.addOrder(1L, order);

        verify(orders).insert(argThat(saved -> new BigDecimal("125.500").equals(saved.getGrossAmountTnd())
            && saved.getQuantityUsdt() == null
            && saved.getUnitPriceTnd() == null));
        verify(trades, never()).updateTotals(anyLong(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    private CreateOrderRequest order(String side, String amount) {
        CreateOrderRequest request = new CreateOrderRequest(); request.setSide(side); request.setRole("TAKER");
        request.setAmountTnd(new BigDecimal(amount)); return request;
    }
}
