package com.store.inventario.module.buy.model.entity;

import java.math.BigDecimal;

public class PurchaseDashboard {
    private BigDecimal todayPurchases;
    private long transactions;
    private BigDecimal averageTicket;

    public PurchaseDashboard() {}

    public PurchaseDashboard(BigDecimal todayPurchases, long transactions, BigDecimal averageTicket) {
        this.todayPurchases = todayPurchases;
        this.transactions = transactions;
        this.averageTicket = averageTicket;
    }

    public BigDecimal getTodayPurchases() { return todayPurchases; }
    public void setTodayPurchases(BigDecimal todayPurchases) { this.todayPurchases = todayPurchases; }

    public long getTransactions() { return transactions; }
    public void setTransactions(long transactions) { this.transactions = transactions; }

    public BigDecimal getAverageTicket() { return averageTicket; }
    public void setAverageTicket(BigDecimal averageTicket) { this.averageTicket = averageTicket; }
}
