package com.edinar.control.dto.p2p;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class FeeRuleResponse {

    private Long id;
    private String platform;
    private String market;
    private String asset;
    private String side;
    private String role;
    private String feeType;
    private BigDecimal feeValue;
    private String feeCurrency;
    private BigDecimal minFee;
    private BigDecimal maxFee;
    private OffsetDateTime effectiveFrom;
    private OffsetDateTime effectiveTo;
    private Boolean isActive;
    private String notes;
    private OffsetDateTime createdAt;

    public FeeRuleResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getMarket() { return market; }
    public void setMarket(String market) { this.market = market; }

    public String getAsset() { return asset; }
    public void setAsset(String asset) { this.asset = asset; }

    public String getSide() { return side; }
    public void setSide(String side) { this.side = side; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFeeType() { return feeType; }
    public void setFeeType(String feeType) { this.feeType = feeType; }

    public BigDecimal getFeeValue() { return feeValue; }
    public void setFeeValue(BigDecimal feeValue) { this.feeValue = feeValue; }

    public String getFeeCurrency() { return feeCurrency; }
    public void setFeeCurrency(String feeCurrency) { this.feeCurrency = feeCurrency; }

    public BigDecimal getMinFee() { return minFee; }
    public void setMinFee(BigDecimal minFee) { this.minFee = minFee; }

    public BigDecimal getMaxFee() { return maxFee; }
    public void setMaxFee(BigDecimal maxFee) { this.maxFee = maxFee; }

    public OffsetDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(OffsetDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public OffsetDateTime getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(OffsetDateTime effectiveTo) { this.effectiveTo = effectiveTo; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
