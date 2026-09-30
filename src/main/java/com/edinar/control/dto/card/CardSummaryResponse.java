package com.edinar.control.dto.card;

import com.edinar.control.dto.transaction.TransactionResponse;

import java.math.BigDecimal;
import java.util.List;

public class CardSummaryResponse {
    private Long cardId;
    private String name;
    private BigDecimal currentBalance;
    private BigDecimal lastVerifiedBalance;
    private long transactionCount;
    private BigDecimal totalIn;
    private BigDecimal totalOut;
    private List<TransactionResponse> recentTransactions;

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
    public BigDecimal getLastVerifiedBalance() { return lastVerifiedBalance; }
    public void setLastVerifiedBalance(BigDecimal lastVerifiedBalance) { this.lastVerifiedBalance = lastVerifiedBalance; }
    public long getTransactionCount() { return transactionCount; }
    public void setTransactionCount(long transactionCount) { this.transactionCount = transactionCount; }
    public BigDecimal getTotalIn() { return totalIn; }
    public void setTotalIn(BigDecimal totalIn) { this.totalIn = totalIn; }
    public BigDecimal getTotalOut() { return totalOut; }
    public void setTotalOut(BigDecimal totalOut) { this.totalOut = totalOut; }
    public List<TransactionResponse> getRecentTransactions() { return recentTransactions; }
    public void setRecentTransactions(List<TransactionResponse> recentTransactions) { this.recentTransactions = recentTransactions; }
}
