package com.store.inventario.model.proveedor;

public record CreateProveedorRequest(
    String taxId,
    String tradeName,
    String phone,
    String legalName
) {}
