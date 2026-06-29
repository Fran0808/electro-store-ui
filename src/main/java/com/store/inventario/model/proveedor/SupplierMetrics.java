package com.store.inventario.model.proveedor;

public class SupplierMetrics {
    private long totalSuppliers;
    private String lastSupplierName;

    public long getTotalSuppliers() {
        return totalSuppliers;
    }

    public void setTotalSuppliers(long totalSuppliers) {
        this.totalSuppliers = totalSuppliers;
    }

    public String getLastSupplierName() {
        return lastSupplierName;
    }

    public void setLastSupplierName(String lastSupplierName) {
        this.lastSupplierName = lastSupplierName;
    }
}
