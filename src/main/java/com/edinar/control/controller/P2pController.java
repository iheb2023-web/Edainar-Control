package com.edinar.control.controller;

import com.edinar.control.dto.p2p.CreateOrderRequest;
import com.edinar.control.dto.p2p.CreateTradeRequest;
import com.edinar.control.dto.p2p.OrderResponse;
import com.edinar.control.dto.p2p.TradeResponse;
import com.edinar.control.dto.p2p.TradeWithOrdersResponse;
import com.edinar.control.service.P2pService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/p2p/trades")
public class P2pController {

    private final P2pService p2pService;

    public P2pController(P2pService p2pService) {
        this.p2pService = p2pService;
    }

    // =========================================================================
    // Trades
    // =========================================================================

    /**
     * POST /api/p2p/trades
     * Créer un nouveau trade P2P (ouvert).
     */
    @PostMapping
    public ResponseEntity<TradeResponse> createTrade(@Valid @RequestBody CreateTradeRequest request) {
        TradeResponse response = p2pService.createTrade(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/p2p/trades
     * Lister tous les trades de l'utilisateur courant.
     */
    @GetMapping
    public ResponseEntity<List<TradeResponse>> getMyTrades() {
        List<TradeResponse> trades = p2pService.getMyTrades();
        return ResponseEntity.ok(trades);
    }

    /**
     * GET /api/p2p/trades/{id}
     * Détail d'un trade avec la liste de ses ordres.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TradeWithOrdersResponse> getTradeById(@PathVariable Long id) {
        TradeWithOrdersResponse response = p2pService.getTradeWithOrders(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/p2p/trades/{id}/close
     * Clôturer un trade ouvert.
     */
    @PatchMapping("/{id}/close")
    public ResponseEntity<TradeResponse> closeTrade(@PathVariable Long id) {
        TradeResponse response = p2pService.closeTrade(id);
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // Orders
    // =========================================================================

    /**
     * POST /api/p2p/trades/{id}/orders
     * Ajouter un ordre (BUY ou SELL) sur un trade ouvert.
     * Le Profit Engine calcule automatiquement les frais et met à jour le ledger.
     */
    @PostMapping("/{id}/orders")
    public ResponseEntity<OrderResponse> addOrder(
        @PathVariable Long id,
        @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderResponse response = p2pService.addOrder(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/p2p/trades/{id}/orders
     * Lister tous les ordres d'un trade.
     */
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponse>> getOrders(@PathVariable Long id) {
        List<OrderResponse> orders = p2pService.getOrdersByTrade(id);
        return ResponseEntity.ok(orders);
    }
}
