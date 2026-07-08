package com.store.inventario.shared.model;

public class TopProduct {
    private String productName;
    private long totalQuantity;

    public TopProduct() {}

    public TopProduct(String productName, long totalQuantity) {
        this.productName = productName;
        this.totalQuantity = totalQuantity;
    }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public long getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(long totalQuantity) { this.totalQuantity = totalQuantity; }
}
