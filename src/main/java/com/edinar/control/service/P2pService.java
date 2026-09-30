package com.edinar.control.service;

import com.edinar.control.domain.Card;
import com.edinar.control.domain.P2pOrder;
import com.edinar.control.domain.P2pTrade;
import com.edinar.control.dto.p2p.CreateOrderRequest;
import com.edinar.control.dto.p2p.CreateTradeRequest;
import com.edinar.control.dto.p2p.OrderResponse;
import com.edinar.control.dto.p2p.TradeResponse;
import com.edinar.control.dto.p2p.TradeWithOrdersResponse;
import com.edinar.control.dto.transaction.CreateTransactionRequest;
import com.edinar.control.exception.BadRequestException;
import com.edinar.control.exception.ResourceNotFoundException;
import com.edinar.control.mapper.CardMapper;
import com.edinar.control.mapper.P2pOrderMapper;
import com.edinar.control.mapper.P2pTradeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class P2pService {

    private final P2pTradeMapper p2pTradeMapper;
    private final P2pOrderMapper p2pOrderMapper;
    private final CardMapper cardMapper;
    private final FeeRuleService feeRuleService;
    private final LedgerService ledgerService;
    private final AuditLogService auditLogService;
    private final CurrentUserService currentUserService;

    public P2pService(
        P2pTradeMapper p2pTradeMapper,
        P2pOrderMapper p2pOrderMapper,
        CardMapper cardMapper,
        FeeRuleService feeRuleService,
        LedgerService ledgerService,
        AuditLogService auditLogService,
        CurrentUserService currentUserService
    ) {
        this.p2pTradeMapper = p2pTradeMapper;
        this.p2pOrderMapper = p2pOrderMapper;
        this.cardMapper = cardMapper;
        this.feeRuleService = feeRuleService;
        this.ledgerService = ledgerService;
        this.auditLogService = auditLogService;
        this.currentUserService = currentUserService;
    }

    // =========================================================================
    // Trades
    // =========================================================================

    @Transactional(rollbackFor = Exception.class)
    public TradeResponse createTrade(CreateTradeRequest request) {
        UUID userId = currentUserService.getCurrentUserId();
        OffsetDateTime now = OffsetDateTime.now();

        // Vérifier la carte si fournie
        if (request.getCardId() != null) {
            cardMapper.findByIdAndUserId(request.getCardId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Carte introuvable avec l'identifiant : " + request.getCardId()));
        }

        P2pTrade trade = new P2pTrade();
        trade.setUserId(userId);
        trade.setCardId(request.getCardId());
        trade.setAsset(request.getAsset() != null ? request.getAsset().toUpperCase() : "USDT");
        trade.setQuoteCurrency(request.getQuoteCurrency() != null ? request.getQuoteCurrency().toUpperCase() : "TND");
        trade.setStatus("OPEN");
        trade.setTotalBuyUsdt(BigDecimal.ZERO);
        trade.setTotalSellUsdt(BigDecimal.ZERO);
        trade.setTotalBuyTnd(BigDecimal.ZERO);
        trade.setTotalSellTnd(BigDecimal.ZERO);
        trade.setTotalBinanceFeeUsdt(BigDecimal.ZERO);
        trade.setTotalBinanceFeeTnd(BigDecimal.ZERO);
        trade.setTotalOtherFeesTnd(BigDecimal.ZERO);
        trade.setGrossProfitTnd(BigDecimal.ZERO);
        trade.setNetProfitTnd(BigDecimal.ZERO);
        trade.setNotes(request.getNotes());
        trade.setOpenedAt(now);
        trade.setClosedAt(null);
        trade.setCreatedAt(now);
        trade.setUpdatedAt(now);

        p2pTradeMapper.insert(trade);

        auditLogService.log(userId, "P2P_TRADE", trade.getId(), "CREATE", null, trade,
            "Ouverture trade P2P " + trade.getAsset() + "/" + trade.getQuoteCurrency());

        return toTradeResponse(trade);
    }

    @Transactional(readOnly = true)
    public List<TradeResponse> getMyTrades() {
        UUID userId = currentUserService.getCurrentUserId();
        return p2pTradeMapper.findByUserId(userId).stream()
            .map(this::toTradeResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public TradeWithOrdersResponse getTradeWithOrders(Long tradeId) {
        P2pTrade trade = requireTrade(tradeId);
        List<OrderResponse> orders = p2pOrderMapper.findByTradeId(tradeId).stream()
            .map(this::toOrderResponse)
            .toList();
        return new TradeWithOrdersResponse(toTradeResponse(trade), orders);
    }

    @Transactional(rollbackFor = Exception.class)
    public TradeResponse closeTrade(Long tradeId) {
        P2pTrade trade = requireTrade(tradeId);
        if (!"OPEN".equalsIgnoreCase(trade.getStatus())) {
            throw new BadRequestException("Le trade est déjà " + trade.getStatus());
        }
        OffsetDateTime now = OffsetDateTime.now();
        p2pTradeMapper.updateStatus(tradeId, "CLOSED", now, now);

        trade.setStatus("CLOSED");
        trade.setClosedAt(now);

        auditLogService.log(trade.getUserId(), "P2P_TRADE", tradeId, "UPDATE", null, trade,
            "Clôture trade P2P #" + tradeId);

        return toTradeResponse(trade);
    }

    // =========================================================================
    // Orders — Profit Engine
    // =========================================================================

    @Transactional(rollbackFor = Exception.class)
    public OrderResponse addOrder(Long tradeId, CreateOrderRequest request) {
        P2pTrade trade = requireTrade(tradeId);

        if (!"OPEN".equalsIgnoreCase(trade.getStatus())) {
            throw new BadRequestException("Impossible d'ajouter un ordre : le trade est " + trade.getStatus());
        }

        String side = request.getSide().toUpperCase();
        String role = request.getRole().toUpperCase();

        if (!side.equals("BUY") && !side.equals("SELL")) {
            throw new BadRequestException("Le côté doit être BUY ou SELL");
        }
        if (!role.equals("MAKER") && !role.equals("TAKER")) {
            throw new BadRequestException("Le rôle doit être MAKER ou TAKER");
        }

        // Déterminer la carte : ordre en priorité, sinon trade, sinon null
        Long cardId = request.getCardId() != null ? request.getCardId() : trade.getCardId();
        Card card = null;
        if (cardId != null) {
            card = cardMapper.findByIdAndUserId(cardId, trade.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Carte introuvable avec l'identifiant : " + cardId));
            if (!"ACTIVE".equalsIgnoreCase(card.getStatus())) {
                throw new BadRequestException("La carte est " + card.getStatus() + " — transaction impossible");
            }
        }

        OffsetDateTime orderDate = request.getOrderDate() != null ? request.getOrderDate() : OffsetDateTime.now();

        BigDecimal amountTnd = request.getAmountTnd();
        BigDecimal binanceFeeUsdt = BigDecimal.ZERO;
        BigDecimal binanceFeeTnd = BigDecimal.ZERO;

        // -----------------------------------------------------------------------
        // INSERT p2p_orders
        // -----------------------------------------------------------------------
        OffsetDateTime now = OffsetDateTime.now();
        P2pOrder order = new P2pOrder();
        order.setTradeId(tradeId);
        order.setCardId(cardId);
        order.setBinanceOrderId(request.getBinanceOrderId());
        order.setSide(side);
        order.setRole(role);
        order.setAsset(trade.getAsset());
        order.setQuoteCurrency(trade.getQuoteCurrency());
        order.setQuantityUsdt(null);
        order.setUnitPriceTnd(null);
        order.setGrossAmountTnd(amountTnd);
        order.setBinanceFeeUsdt(binanceFeeUsdt);
        order.setBinanceFeeTnd(binanceFeeTnd);
        order.setFeeConversionRate(request.getFeeConversionRate());
        order.setStatus("COMPLETED");
        order.setNotes(request.getNotes());
        order.setOrderDate(orderDate);
        order.setCreatedAt(now);

        p2pOrderMapper.insert(order);

        // Le montant est enregistré tel quel et reflété dans le ledger de la carte.
        if (card != null) {
            CreateTransactionRequest ledgerRequest = new CreateTransactionRequest();
            ledgerRequest.setType("BUY".equals(side) ? "P2P_BUY" : "P2P_SELL");
            ledgerRequest.setAmount(amountTnd);
            ledgerRequest.setFeeAmount(binanceFeeTnd);
            ledgerRequest.setDescription(String.format("P2P %s - montant %s TND", side, amountTnd.toPlainString()));
            ledgerRequest.setReference("P2P-ORDER-" + order.getId());
            ledgerRequest.setTransactionDate(orderDate);

            ledgerService.createTransaction(cardId, ledgerRequest);
        }

        auditLogService.log(trade.getUserId(), "P2P_ORDER", order.getId(), "CREATE", null, order,
            String.format("Ordre P2P %s - montant %s %s", side, amountTnd, trade.getQuoteCurrency()));

        return toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByTrade(Long tradeId) {
        requireTrade(tradeId);
        return p2pOrderMapper.findByTradeId(tradeId).stream()
            .map(this::toOrderResponse)
            .toList();
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private P2pTrade requireTrade(Long tradeId) {
        UUID userId = currentUserService.getCurrentUserId();
        return p2pTradeMapper.findById(tradeId)
            .filter(trade -> userId.equals(trade.getUserId()))
            .orElseThrow(() -> new ResourceNotFoundException(
                "Trade P2P introuvable avec l'identifiant : " + tradeId));
    }

    private TradeResponse toTradeResponse(P2pTrade t) {
        TradeResponse r = new TradeResponse();
        r.setId(t.getId());
        r.setUserId(t.getUserId());
        r.setCardId(t.getCardId());
        r.setAsset(t.getAsset());
        r.setQuoteCurrency(t.getQuoteCurrency());
        r.setStatus(t.getStatus());
        r.setTotalBuyUsdt(t.getTotalBuyUsdt());
        r.setTotalSellUsdt(t.getTotalSellUsdt());
        r.setTotalBuyTnd(t.getTotalBuyTnd());
        r.setTotalSellTnd(t.getTotalSellTnd());
        r.setTotalBinanceFeeUsdt(t.getTotalBinanceFeeUsdt());
        r.setTotalBinanceFeeTnd(t.getTotalBinanceFeeTnd());
        r.setTotalOtherFeesTnd(t.getTotalOtherFeesTnd());
        boolean realized = "CLOSED".equalsIgnoreCase(t.getStatus());
        r.setGrossProfitTnd(realized ? t.getGrossProfitTnd() : BigDecimal.ZERO);
        r.setNetProfitTnd(realized ? t.getNetProfitTnd() : BigDecimal.ZERO);
        r.setNotes(t.getNotes());
        r.setOpenedAt(t.getOpenedAt());
        r.setClosedAt(t.getClosedAt());
        r.setCreatedAt(t.getCreatedAt());
        r.setUpdatedAt(t.getUpdatedAt());
        return r;
    }

    private OrderResponse toOrderResponse(P2pOrder o) {
        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setTradeId(o.getTradeId());
        r.setCardId(o.getCardId());
        r.setBinanceOrderId(o.getBinanceOrderId());
        r.setSide(o.getSide());
        r.setRole(o.getRole());
        r.setAsset(o.getAsset());
        r.setQuoteCurrency(o.getQuoteCurrency());
        r.setQuantityUsdt(o.getQuantityUsdt());
        r.setUnitPriceTnd(o.getUnitPriceTnd());
        r.setGrossAmountTnd(o.getGrossAmountTnd());
        r.setBinanceFeeUsdt(o.getBinanceFeeUsdt());
        r.setBinanceFeeTnd(o.getBinanceFeeTnd());
        r.setFeeConversionRate(o.getFeeConversionRate());
        r.setStatus(o.getStatus());
        r.setNotes(o.getNotes());
        r.setOrderDate(o.getOrderDate());
        r.setCreatedAt(o.getCreatedAt());
        return r;
    }
}
