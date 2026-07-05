package com.store.inventario.module.buy.model.entity;

import com.store.inventario.module.product.model.entity.Product;


import java.math.BigDecimal;

public class PurchaseDetail {
    private String code;
    private Product product;
    private BigDecimal purchasePrice;
    private int quantity;

    public PurchaseDetail(){ }

    public PurchaseDetail(String code, Product product, BigDecimal purchasePrice, int quantity) {
        this.code = code;
        this.product = product;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
    }

    public String getCode() {
        return code;
    }

    public Product getProduct() {
        return product;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return purchasePrice != null ? purchasePrice.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
    }
}
