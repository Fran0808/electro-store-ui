package com.store.inventario.module.product.request;

import java.math.BigDecimal;

public record CreateProductRequest(
    String categoryCode,
    String name,
    String brand,
    String model,
    BigDecimal salePrice,
    String description,
    Integer warrantyMonths,
    Integer lowStock
) {}
