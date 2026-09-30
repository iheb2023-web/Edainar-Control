package com.edinar.control.dto.p2p;

import jakarta.validation.constraints.Size;

public class CreateTradeRequest {

    private Long cardId; // facultatif

    @Size(max = 20)
    private String asset; // défaut: USDT

    @Size(max = 3)
    private String quoteCurrency; // défaut: TND

    @Size(max = 1000)
    private String notes;

    public CreateTradeRequest() {}

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }

    public String getAsset() { return asset; }
    public void setAsset(String asset) { this.asset = asset; }

    public String getQuoteCurrency() { return quoteCurrency; }
    public void setQuoteCurrency(String quoteCurrency) { this.quoteCurrency = quoteCurrency; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
