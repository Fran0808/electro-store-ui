package com.store.inventario.model.ventas;

import com.store.inventario.model.producto.Producto;
import java.math.BigDecimal;

public class VentaDetalle {
    private String code;
    private Producto product;
    private BigDecimal salePrice;
    private int quantity;

    public VentaDetalle() {}

    public VentaDetalle(String code, Producto product, BigDecimal salePrice, int quantity) {
        this.code = code;
        this.product = product;
        this.salePrice = salePrice;
        this.quantity = quantity;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Producto getProduct() {
        return product;
    }

    public void setProduct(Producto product) {
        this.product = product;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return salePrice != null ? salePrice.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
    }
}
