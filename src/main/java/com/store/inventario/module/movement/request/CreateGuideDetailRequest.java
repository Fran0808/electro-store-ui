package com.store.inventario.module.movement.request;

public record CreateGuideDetailRequest(
    String productCode,
    Integer quantity
) {}
