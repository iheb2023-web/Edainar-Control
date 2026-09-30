package com.edinar.control.dto.p2p;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateFeeRuleRequest {

    @NotBlank(message = "La plateforme est obligatoire")
    @Size(max = 50)
    private String platform; // Binance

    @NotBlank(message = "Le marché est obligatoire")
    @Size(max = 30)
    private String market; // TND

    @NotBlank(message = "L'actif est obligatoire")
    @Size(max = 20)
    private String asset; // USDT

    @Size(max = 10)
    private String side; // BUY, SELL, null = both

    @Size(max = 10)
    private String role; // MAKER, TAKER, null = both

    @NotBlank(message = "Le type de frais est obligatoire (PERCENTAGE ou FIXED)")
    private String feeType;

    @NotNull(message = "La valeur des frais est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal feeValue;

    @NotBlank(message = "La devise des frais est obligatoire")
    @Size(max = 10)
    private String feeCurrency; // USDT

    private BigDecimal minFee;
    private BigDecimal maxFee;

    @Size(max = 1000)
    private String notes;

    public CreateFeeRuleRequest() {}

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

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
