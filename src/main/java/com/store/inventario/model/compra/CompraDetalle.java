package com.store.inventario.model.compra;

import com.store.inventario.module.product.model.entity.Product;


import java.math.BigDecimal;

public class CompraDetalle {
    private String code;
    private Product product;
    private BigDecimal purchasePrice;
    private int quantity;

    public CompraDetalle(){ }

    public CompraDetalle(String code, Product product, BigDecimal purchasePrice, int quantity) {
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
