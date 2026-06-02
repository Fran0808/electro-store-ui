package com.store.inventario.model.proveedor;

public record UpdateProveedorRequest(
    String taxId,
    String tradeName,
    String phone,
    String legalName
) {}
