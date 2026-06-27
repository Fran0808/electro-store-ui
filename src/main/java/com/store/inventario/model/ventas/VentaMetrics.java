package com.store.inventario.model.ventas;

import java.math.BigDecimal;

public class VentaMetrics {
    private BigDecimal todaySales;
    private long transactions;
    private BigDecimal averageTicket;

    public VentaMetrics() {}

    public VentaMetrics(BigDecimal todaySales, long transactions, BigDecimal averageTicket) {
        this.todaySales = todaySales;
        this.transactions = transactions;
        this.averageTicket = averageTicket;
    }

    public BigDecimal getTodaySales() {
        return todaySales;
    }

    public void setTodaySales(BigDecimal todaySales) {
        this.todaySales = todaySales;
    }

    public long getTransactions() {
        return transactions;
    }

    public void setTransactions(long transactions) {
        this.transactions = transactions;
    }

    public BigDecimal getAverageTicket() {
        return averageTicket;
    }

    public void setAverageTicket(BigDecimal averageTicket) {
        this.averageTicket = averageTicket;
    }
}
