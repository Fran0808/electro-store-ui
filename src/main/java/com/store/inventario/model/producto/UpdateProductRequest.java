package com.store.inventario.model.producto;

import java.math.BigDecimal;

public class UpdateProductRequest {
    private String categoryCode;
    private String name;
    private String brand;
    private String model;
    private BigDecimal salePrice;
    private String description;
    private Integer warrantyMonths;

    public UpdateProductRequest(String categoryCode, String name, String brand, String model, BigDecimal salePrice, String description, Integer warrantyMonths) {
        this.categoryCode = categoryCode;
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.salePrice = salePrice;
        this.description = description;
        this.warrantyMonths = warrantyMonths;
    }

    public String getCategoryCode() { return categoryCode; }
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public BigDecimal getSalePrice() { return salePrice; }
    public String getDescription() { return description; }
    public Integer getWarrantyMonths() { return warrantyMonths; }
}
