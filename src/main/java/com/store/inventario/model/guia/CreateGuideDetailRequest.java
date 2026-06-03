package com.store.inventario.model.guia;

public record CreateGuideDetailRequest(
    String productCode,
    Integer quantity
) {}
