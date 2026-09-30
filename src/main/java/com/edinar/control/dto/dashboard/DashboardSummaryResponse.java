package com.edinar.control.dto.dashboard;

import com.edinar.control.dto.transaction.TransactionResponse;

import java.math.BigDecimal;
import java.util.List;

public class DashboardSummaryResponse {
    private long totalCards;
    private long activeCards;
    private BigDecimal totalBalance;
    private BigDecimal totalIn;
    private BigDecimal totalOut;
    private BigDecimal todayIn;
    private BigDecimal todayOut;
    private List<TransactionResponse> recentTransactions;

    public long getTotalCards() { return totalCards; }
    public void setTotalCards(long totalCards) { this.totalCards = totalCards; }
    public long getActiveCards() { return activeCards; }
    public void setActiveCards(long activeCards) { this.activeCards = activeCards; }
    public BigDecimal getTotalBalance() { return totalBalance; }
    public void setTotalBalance(BigDecimal totalBalance) { this.totalBalance = totalBalance; }
    public BigDecimal getTotalIn() { return totalIn; }
    public void setTotalIn(BigDecimal totalIn) { this.totalIn = totalIn; }
    public BigDecimal getTotalOut() { return totalOut; }
    public void setTotalOut(BigDecimal totalOut) { this.totalOut = totalOut; }
    public BigDecimal getTodayIn() { return todayIn; }
    public void setTodayIn(BigDecimal todayIn) { this.todayIn = todayIn; }
    public BigDecimal getTodayOut() { return todayOut; }
    public void setTodayOut(BigDecimal todayOut) { this.todayOut = todayOut; }
    public List<TransactionResponse> getRecentTransactions() { return recentTransactions; }
    public void setRecentTransactions(List<TransactionResponse> recentTransactions) { this.recentTransactions = recentTransactions; }
}
