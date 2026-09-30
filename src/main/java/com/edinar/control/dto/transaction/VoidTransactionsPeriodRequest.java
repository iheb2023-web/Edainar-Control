package com.edinar.control.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public class VoidTransactionsPeriodRequest {

    @NotNull(message = "La date de début est obligatoire")
    private OffsetDateTime from;

    @NotNull(message = "La date de fin est obligatoire")
    private OffsetDateTime to;

    @NotBlank(message = "La raison d'annulation est obligatoire")
    @Size(max = 255, message = "La raison ne peut pas dépasser 255 caractères")
    private String reason;

    public VoidTransactionsPeriodRequest() {}

    public OffsetDateTime getFrom() { return from; }
    public void setFrom(OffsetDateTime from) { this.from = from; }
    public OffsetDateTime getTo() { return to; }
    public void setTo(OffsetDateTime to) { this.to = to; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}