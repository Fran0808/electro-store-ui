package com.store.inventario.model.compra;

import com.store.inventario.model.producto.Producto;


import java.math.BigDecimal;

public class CompraDetalle {
    private String code;
    private Producto product;
    private BigDecimal purchasePrice;
    private int quantity;

    public CompraDetalle(){ }

    public CompraDetalle(String code, Producto product, BigDecimal purchasePrice, int quantity) {
        this.code = code;
        this.product = product;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
    }

    public String getCode() {
        return code;
    }

    public Producto getProduct() {
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
