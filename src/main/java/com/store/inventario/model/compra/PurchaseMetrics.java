package com.store.inventario.model.compra;

public class PurchaseMetrics {
    private long weeklyPurchases;
    private long monthlyProductsEntered;
    private String frequentSupplierName;
    private long frequentSupplierCount;

    public long getWeeklyPurchases() {
        return weeklyPurchases;
    }

    public void setWeeklyPurchases(long weeklyPurchases) {
        this.weeklyPurchases = weeklyPurchases;
    }

    public long getMonthlyProductsEntered() {
        return monthlyProductsEntered;
    }

    public void setMonthlyProductsEntered(long monthlyProductsEntered) {
        this.monthlyProductsEntered = monthlyProductsEntered;
    }

    public String getFrequentSupplierName() {
        return frequentSupplierName;
    }

    public void setFrequentSupplierName(String frequentSupplierName) {
        this.frequentSupplierName = frequentSupplierName;
    }

    public long getFrequentSupplierCount() {
        return frequentSupplierCount;
    }

    public void setFrequentSupplierCount(long frequentSupplierCount) {
        this.frequentSupplierCount = frequentSupplierCount;
    }
}
