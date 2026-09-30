package com.edinar.control.dto.reconcile;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class ReconcileResponse {
    private Long cardId;
    private BigDecimal calculatedBalance;
    private BigDecimal realBalance;
    private BigDecimal difference;
    private String adjustmentType; // NONE, POSITIVE, NEGATIVE
    private Long adjustmentTransactionId;
    private OffsetDateTime reconciledAt;

    public ReconcileResponse() {}

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public BigDecimal getCalculatedBalance() { return calculatedBalance; }
    public void setCalculatedBalance(BigDecimal calculatedBalance) { this.calculatedBalance = calculatedBalance; }
    public BigDecimal getRealBalance() { return realBalance; }
    public void setRealBalance(BigDecimal realBalance) { this.realBalance = realBalance; }
    public BigDecimal getDifference() { return difference; }
    public void setDifference(BigDecimal difference) { this.difference = difference; }
    public String getAdjustmentType() { return adjustmentType; }
    public void setAdjustmentType(String adjustmentType) { this.adjustmentType = adjustmentType; }
    public Long getAdjustmentTransactionId() { return adjustmentTransactionId; }
    public void setAdjustmentTransactionId(Long adjustmentTransactionId) { this.adjustmentTransactionId = adjustmentTransactionId; }
    public OffsetDateTime getReconciledAt() { return reconciledAt; }
    public void setReconciledAt(OffsetDateTime reconciledAt) { this.reconciledAt = reconciledAt; }
}
