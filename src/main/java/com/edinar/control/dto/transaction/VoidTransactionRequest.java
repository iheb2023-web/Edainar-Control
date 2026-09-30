package com.edinar.control.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VoidTransactionRequest {

    @NotBlank(message = "La raison d'annulation est obligatoire")
    @Size(max = 255, message = "La raison ne peut pas dépasser 255 caractères")
    private String reason;

    public VoidTransactionRequest() {}

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
