package com.store.inventario.model.ventas;

import com.store.inventario.module.product.model.entity.Product;
import java.math.BigDecimal;

public class VentaDetalle {
    private String code;
    private Product product;
    private BigDecimal salePrice;
    private int quantity;

    public VentaDetalle() {}

    public VentaDetalle(String code, Product product, BigDecimal salePrice, int quantity) {
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
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
