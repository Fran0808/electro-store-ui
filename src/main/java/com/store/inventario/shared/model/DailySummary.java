package com.store.inventario.shared.model;

import java.math.BigDecimal;

public class DailySummary {
    private String date;
    private BigDecimal total;

    public DailySummary() {}

    public DailySummary(String date, BigDecimal total) {
        this.date = date;
        this.total = total;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
