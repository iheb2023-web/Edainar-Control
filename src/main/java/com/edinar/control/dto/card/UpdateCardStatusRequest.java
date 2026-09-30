package com.edinar.control.dto.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateCardStatusRequest {

    @NotBlank(message = "Le statut est obligatoire")
    @Pattern(regexp = "^(ACTIVE|BLOCKED|CLOSED)$", message = "Le statut doit être ACTIVE, BLOCKED ou CLOSED")
    private String status;

    public UpdateCardStatusRequest() {
    }

    public UpdateCardStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
