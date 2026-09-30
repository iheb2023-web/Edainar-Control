package com.edinar.control.dto.p2p;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class OrderResponse {

    private Long id;
    private Long tradeId;
    private Long cardId;
    private String binanceOrderId;
    private String side;
    private String role;
    private String asset;
    private String quoteCurrency;

    private BigDecimal quantityUsdt;
    private BigDecimal unitPriceTnd;
    private BigDecimal grossAmountTnd;

    private BigDecimal binanceFeeUsdt;
    private BigDecimal binanceFeeTnd;
    private BigDecimal feeConversionRate;

    private String status;
    private String notes;
    private OffsetDateTime orderDate;
    private OffsetDateTime createdAt;

    public OrderResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTradeId() { return tradeId; }
    public void setTradeId(Long tradeId) { this.tradeId = tradeId; }

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }

    public String getBinanceOrderId() { return binanceOrderId; }
    public void setBinanceOrderId(String binanceOrderId) { this.binanceOrderId = binanceOrderId; }

    public String getSide() { return side; }
    public void setSide(String side) { this.side = side; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAsset() { return asset; }
    public void setAsset(String asset) { this.asset = asset; }

    public String getQuoteCurrency() { return quoteCurrency; }
    public void setQuoteCurrency(String quoteCurrency) { this.quoteCurrency = quoteCurrency; }

    public BigDecimal getQuantityUsdt() { return quantityUsdt; }
    public void setQuantityUsdt(BigDecimal quantityUsdt) { this.quantityUsdt = quantityUsdt; }

    public BigDecimal getUnitPriceTnd() { return unitPriceTnd; }
    public void setUnitPriceTnd(BigDecimal unitPriceTnd) { this.unitPriceTnd = unitPriceTnd; }

    public BigDecimal getGrossAmountTnd() { return grossAmountTnd; }
    public void setGrossAmountTnd(BigDecimal grossAmountTnd) { this.grossAmountTnd = grossAmountTnd; }

    public BigDecimal getBinanceFeeUsdt() { return binanceFeeUsdt; }
    public void setBinanceFeeUsdt(BigDecimal binanceFeeUsdt) { this.binanceFeeUsdt = binanceFeeUsdt; }

    public BigDecimal getBinanceFeeTnd() { return binanceFeeTnd; }
    public void setBinanceFeeTnd(BigDecimal binanceFeeTnd) { this.binanceFeeTnd = binanceFeeTnd; }

    public BigDecimal getFeeConversionRate() { return feeConversionRate; }
    public void setFeeConversionRate(BigDecimal feeConversionRate) { this.feeConversionRate = feeConversionRate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public OffsetDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(OffsetDateTime orderDate) { this.orderDate = orderDate; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
