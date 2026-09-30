package com.edinar.control.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class LimitRule {

    private Long id;
    private Long cardId;
    private String operationType; // RECEIVE, SEND, etc.
    private String periodType;    // DAILY, MONTHLY
    private BigDecimal limitAmount;
    private BigDecimal warningPercentage;
    private BigDecimal criticalPercentage;
    private OffsetDateTime effectiveFrom;
    private OffsetDateTime effectiveTo;
    private Boolean isActive;
    private OffsetDateTime createdAt;

    public LimitRule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public BigDecimal getWarningPercentage() {
        return warningPercentage;
    }

    public void setWarningPercentage(BigDecimal warningPercentage) {
        this.warningPercentage = warningPercentage;
    }

    public BigDecimal getCriticalPercentage() {
        return criticalPercentage;
    }

    public void setCriticalPercentage(BigDecimal criticalPercentage) {
        this.criticalPercentage = criticalPercentage;
    }

    public OffsetDateTime getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(OffsetDateTime effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public OffsetDateTime getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(OffsetDateTime effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
