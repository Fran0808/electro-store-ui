package com.store.inventario.module.person.model.entity;

public class CustomerMetrics {
    private long totalCustomers;
    private long totalWithDni;
    private long totalWithRuc;

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getTotalWithDni() {
        return totalWithDni;
    }

    public void setTotalWithDni(long totalWithDni) {
        this.totalWithDni = totalWithDni;
    }

    public long getTotalWithRuc() {
        return totalWithRuc;
    }

    public void setTotalWithRuc(long totalWithRuc) {
        this.totalWithRuc = totalWithRuc;
    }
}
