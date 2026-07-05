package com.store.inventario.module.supplier.request;

public record UpdateSupplierRequest(
    String taxId,
    String tradeName,
    String phone,
    String legalName
) {}
