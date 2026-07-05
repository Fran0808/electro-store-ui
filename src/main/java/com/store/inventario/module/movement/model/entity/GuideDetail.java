package com.store.inventario.module.movement.model.entity;

import com.store.inventario.module.product.model.entity.Product;

public class GuideDetail {
    private String code;
    private Product product;
    private Integer quantity;

    public GuideDetail() {
    }

    public GuideDetail(String code, Product product, Integer quantity) {
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
