package com.edinar.control.dto.limit;

import java.util.List;

public class CardLimitsResponse {

    private Long cardId;
    private String cardName;
    private List<LimitUsageDto> limits;

    public CardLimitsResponse() {
    }

    public CardLimitsResponse(Long cardId, String cardName, List<LimitUsageDto> limits) {
        this.cardId = cardId;
        this.cardName = cardName;
        this.limits = limits;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public List<LimitUsageDto> getLimits() {
        return limits;
    }

    public void setLimits(List<LimitUsageDto> limits) {
        this.limits = limits;
    }
}
