package com.edinar.control.dto.p2p;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TradeResponse {

    private Long id;
    private UUID userId;
    private Long cardId;
    private String asset;
    private String quoteCurrency;
    private String status;

    private BigDecimal totalBuyUsdt;
    private BigDecimal totalSellUsdt;
    private BigDecimal totalBuyTnd;
    private BigDecimal totalSellTnd;
    private BigDecimal totalBinanceFeeUsdt;
    private BigDecimal totalBinanceFeeTnd;
    private BigDecimal totalOtherFeesTnd;
    private BigDecimal grossProfitTnd;
    private BigDecimal netProfitTnd;

    private String notes;
    private OffsetDateTime openedAt;
    private OffsetDateTime closedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public TradeResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }

    public String getAsset() { return asset; }
    public void setAsset(String asset) { this.asset = asset; }

    public String getQuoteCurrency() { return quoteCurrency; }
    public void setQuoteCurrency(String quoteCurrency) { this.quoteCurrency = quoteCurrency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalBuyUsdt() { return totalBuyUsdt; }
    public void setTotalBuyUsdt(BigDecimal totalBuyUsdt) { this.totalBuyUsdt = totalBuyUsdt; }

    public BigDecimal getTotalSellUsdt() { return totalSellUsdt; }
    public void setTotalSellUsdt(BigDecimal totalSellUsdt) { this.totalSellUsdt = totalSellUsdt; }

    public BigDecimal getTotalBuyTnd() { return totalBuyTnd; }
    public void setTotalBuyTnd(BigDecimal totalBuyTnd) { this.totalBuyTnd = totalBuyTnd; }

    public BigDecimal getTotalSellTnd() { return totalSellTnd; }
    public void setTotalSellTnd(BigDecimal totalSellTnd) { this.totalSellTnd = totalSellTnd; }

    public BigDecimal getTotalBinanceFeeUsdt() { return totalBinanceFeeUsdt; }
    public void setTotalBinanceFeeUsdt(BigDecimal totalBinanceFeeUsdt) { this.totalBinanceFeeUsdt = totalBinanceFeeUsdt; }

    public BigDecimal getTotalBinanceFeeTnd() { return totalBinanceFeeTnd; }
    public void setTotalBinanceFeeTnd(BigDecimal totalBinanceFeeTnd) { this.totalBinanceFeeTnd = totalBinanceFeeTnd; }

    public BigDecimal getTotalOtherFeesTnd() { return totalOtherFeesTnd; }
    public void setTotalOtherFeesTnd(BigDecimal totalOtherFeesTnd) { this.totalOtherFeesTnd = totalOtherFeesTnd; }

    public BigDecimal getGrossProfitTnd() { return grossProfitTnd; }
    public void setGrossProfitTnd(BigDecimal grossProfitTnd) { this.grossProfitTnd = grossProfitTnd; }

    public BigDecimal getNetProfitTnd() { return netProfitTnd; }
    public void setNetProfitTnd(BigDecimal netProfitTnd) { this.netProfitTnd = netProfitTnd; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public OffsetDateTime getOpenedAt() { return openedAt; }
    public void setOpenedAt(OffsetDateTime openedAt) { this.openedAt = openedAt; }

    public OffsetDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(OffsetDateTime closedAt) { this.closedAt = closedAt; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
