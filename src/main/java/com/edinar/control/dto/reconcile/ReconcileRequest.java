package com.edinar.control.dto.reconcile;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ReconcileRequest {

    @NotNull(message = "Le solde réel vérifié est obligatoire")
    private BigDecimal realBalance;

    @Size(max = 255)
    private String reason;

    public ReconcileRequest() {}

    public BigDecimal getRealBalance() { return realBalance; }
    public void setRealBalance(BigDecimal realBalance) { this.realBalance = realBalance; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
