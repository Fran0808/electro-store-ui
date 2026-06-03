package com.store.inventario.model.guia;

import com.store.inventario.model.producto.Producto;

public class GuideDetail {
    private String code;
    private Producto product;
    private Integer quantity;

    public GuideDetail() {
    }

    public GuideDetail(String code, Producto product, Integer quantity) {
        this.code = code;
        this.product = product;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
