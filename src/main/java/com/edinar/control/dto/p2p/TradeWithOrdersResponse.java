package com.edinar.control.dto.p2p;

import java.util.List;

public class TradeWithOrdersResponse {

    private TradeResponse trade;
    private List<OrderResponse> orders;

    public TradeWithOrdersResponse() {}

    public TradeWithOrdersResponse(TradeResponse trade, List<OrderResponse> orders) {
        this.trade = trade;
        this.orders = orders;
    }

    public TradeResponse getTrade() { return trade; }
    public void setTrade(TradeResponse trade) { this.trade = trade; }

    public List<OrderResponse> getOrders() { return orders; }
    public void setOrders(List<OrderResponse> orders) { this.orders = orders; }
}
