package com.store.inventario.model.ventas;

public record CreateSaleDetailRequest(
    String productCode,
    Integer quantity
) {}
