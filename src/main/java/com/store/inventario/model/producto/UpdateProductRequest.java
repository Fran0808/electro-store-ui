package com.store.inventario.model.producto;

import java.math.BigDecimal;

public record UpdateProductRequest(
    String categoryCode,
    String name,
    String brand,
    String model,
    BigDecimal salePrice,
    String description,
    Integer warrantyMonths,
    Integer lowStock
) {}
