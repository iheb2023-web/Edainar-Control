package com.edinar.control.dto.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class CreateTransactionRequest {

    @NotBlank(message = "Le type de transaction est obligatoire")
    @Size(max = 30)
    private String type; // RECEIVE, SEND, ATM_WITHDRAWAL, ONLINE_PAYMENT, CARD_PAYMENT, P2P_BUY, P2P_SELL, FEE, INTERNAL_TRANSFER, ADJUSTMENT

    private String direction; // IN ou OUT (facultatif si déductible du type)

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.001", message = "Le montant doit être supérieur à 0")
    private BigDecimal amount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Le montant des frais ne peut pas être négatif")
    private BigDecimal feeAmount;

    private Long categoryId;

    @Size(max = 255)
    private String description;

    @Size(max = 100)
    private String reference;

    private OffsetDateTime transactionDate;

    public CreateTransactionRequest() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public OffsetDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(OffsetDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
