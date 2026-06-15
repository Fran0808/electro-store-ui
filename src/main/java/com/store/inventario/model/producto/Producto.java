package com.store.inventario.model.producto;

import java.math.BigDecimal;

public class Producto {
    private String code;
    private String categoryName;
    private String name;
    private String brand;
    private String model;
    private BigDecimal salePrice;
    private Integer stock;
    private String description;
    private Integer warrantyMonths;

    private Integer lowStock;

    public Producto() {
    }

    public Producto(String code, String categoryName, String name, String brand, String model, BigDecimal salePrice, Integer stock, String description, Integer warrantyMonths) {
        this.code = code;
        this.categoryName = categoryName;
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.salePrice = salePrice;
        this.stock = stock;
        this.description = description;
        this.warrantyMonths = warrantyMonths;
    }

    public Producto(String code, String categoryName, String name, String brand, String model, BigDecimal salePrice, Integer stock, String description, Integer warrantyMonths, Integer lowStock) {
        this.code = code;
        this.categoryName = categoryName;
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.salePrice = salePrice;
        this.stock = stock;
        this.description = description;
        this.warrantyMonths = warrantyMonths;
        this.lowStock = lowStock;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(Integer warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public Integer getLowStock() {
        return lowStock;
    }

    public void setLowStock(Integer lowStock) {
        this.lowStock = lowStock;
    }
}
