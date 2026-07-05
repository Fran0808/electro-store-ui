package com.store.inventario.module.sale.request;

public record CreateSaleDetailRequest(
    String productCode,
    Integer quantity
) {}
