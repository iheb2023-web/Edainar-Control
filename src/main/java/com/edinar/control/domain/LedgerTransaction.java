package com.edinar.control.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class LedgerTransaction {

    private Long id;
    private Long cardId;
    private String type;
    private String direction;
    private BigDecimal amount;
    private BigDecimal feeAmount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private Long categoryId;
    private String description;
    private String reference;
    private OffsetDateTime transactionDate;

    // Transfert interne — les deux legs partagent le même transferGroupId
    private UUID transferGroupId;

    // VOID — jamais de suppression physique
    private Boolean isVoid = false;
    private String voidReason;
    private OffsetDateTime voidedAt;
    private UUID voidedBy;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public LedgerTransaction() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getFeeAmount() { return feeAmount; }
    public void setFeeAmount(BigDecimal feeAmount) { this.feeAmount = feeAmount; }

    public BigDecimal getBalanceBefore() { return balanceBefore; }
    public void setBalanceBefore(BigDecimal balanceBefore) { this.balanceBefore = balanceBefore; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public OffsetDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(OffsetDateTime transactionDate) { this.transactionDate = transactionDate; }

    public UUID getTransferGroupId() { return transferGroupId; }
    public void setTransferGroupId(UUID transferGroupId) { this.transferGroupId = transferGroupId; }

    public Boolean getIsVoid() { return isVoid; }
    public void setIsVoid(Boolean isVoid) { this.isVoid = isVoid; }

    public String getVoidReason() { return voidReason; }
    public void setVoidReason(String voidReason) { this.voidReason = voidReason; }

    public OffsetDateTime getVoidedAt() { return voidedAt; }
    public void setVoidedAt(OffsetDateTime voidedAt) { this.voidedAt = voidedAt; }

    public UUID getVoidedBy() { return voidedBy; }
    public void setVoidedBy(UUID voidedBy) { this.voidedBy = voidedBy; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
