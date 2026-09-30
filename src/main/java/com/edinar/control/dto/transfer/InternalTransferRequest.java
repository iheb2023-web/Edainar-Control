package com.edinar.control.dto.transfer;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class InternalTransferRequest {

    @NotNull(message = "La carte source est obligatoire")
    private Long sourceCardId;

    @NotNull(message = "La carte destination est obligatoire")
    private Long destinationCardId;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.001", message = "Le montant doit être supérieur à 0")
    private BigDecimal amount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Les frais ne peuvent pas être négatifs")
    private BigDecimal feeAmount;

    @Size(max = 255)
    private String description;

    @Size(max = 100)
    private String reference;

    public InternalTransferRequest() {}

    public Long getSourceCardId() { return sourceCardId; }
    public void setSourceCardId(Long sourceCardId) { this.sourceCardId = sourceCardId; }
    public Long getDestinationCardId() { return destinationCardId; }
    public void setDestinationCardId(Long destinationCardId) { this.destinationCardId = destinationCardId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getFeeAmount() { return feeAmount; }
    public void setFeeAmount(BigDecimal feeAmount) { this.feeAmount = feeAmount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
}
