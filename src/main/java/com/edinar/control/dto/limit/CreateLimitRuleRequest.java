package com.edinar.control.dto.limit;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class CreateLimitRuleRequest {

    @NotBlank(message = "Le type d'opération est obligatoire")
    private String operationType; // RECEIVE, SEND, ATM_WITHDRAWAL, etc.

    @NotBlank(message = "Le type de période est obligatoire")
    @Pattern(regexp = "^(DAILY|MONTHLY)$", message = "La période doit être DAILY ou MONTHLY")
    private String periodType;

    @NotNull(message = "Le montant du plafond est obligatoire")
    @DecimalMin(value = "0.001", message = "Le plafond doit être supérieur à 0")
    private BigDecimal limitAmount;

    @DecimalMin("1.0")
    @DecimalMax("100.0")
    private BigDecimal warningPercentage = new BigDecimal("80.00");

    @DecimalMin("1.0")
    @DecimalMax("100.0")
    private BigDecimal criticalPercentage = new BigDecimal("90.00");

    public CreateLimitRuleRequest() {
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
}
