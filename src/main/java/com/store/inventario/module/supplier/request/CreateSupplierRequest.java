package com.store.inventario.module.supplier.request;

public record CreateSupplierRequest(
    String taxId,
    String tradeName,
    String phone,
    String legalName
) {}
