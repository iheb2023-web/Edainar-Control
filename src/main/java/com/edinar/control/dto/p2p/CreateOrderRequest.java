package com.edinar.control.dto.p2p;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class CreateOrderRequest {

    @NotBlank(message = "Le côté est obligatoire (BUY ou SELL)")
    private String side; // BUY, SELL

    @NotBlank(message = "Le rôle est obligatoire (MAKER ou TAKER)")
    private String role; // MAKER, TAKER

    @NotNull(message = "Le montant TND est obligatoire")
    @DecimalMin(value = "0.001", message = "Le montant doit être positif")
    private BigDecimal amountTnd;

    /** Taux saisi pour la traçabilité de l'ordre, sans calcul automatique. */
    private BigDecimal feeConversionRate;

    /**
     * Si null, la carte du trade est utilisée.
     * Permet de spécifier une carte différente par ordre.
     */
    private Long cardId;

    @Size(max = 100)
    private String binanceOrderId;

    @Size(max = 1000)
    private String notes;

    private OffsetDateTime orderDate;

    public CreateOrderRequest() {}

    public String getSide() { return side; }
    public void setSide(String side) { this.side = side; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public BigDecimal getAmountTnd() { return amountTnd; }
    public void setAmountTnd(BigDecimal amountTnd) { this.amountTnd = amountTnd; }

    public BigDecimal getFeeConversionRate() { return feeConversionRate; }
    public void setFeeConversionRate(BigDecimal feeConversionRate) { this.feeConversionRate = feeConversionRate; }

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }

    public String getBinanceOrderId() { return binanceOrderId; }
    public void setBinanceOrderId(String binanceOrderId) { this.binanceOrderId = binanceOrderId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public OffsetDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(OffsetDateTime orderDate) { this.orderDate = orderDate; }
}
