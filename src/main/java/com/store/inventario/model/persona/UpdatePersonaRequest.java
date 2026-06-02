package com.store.inventario.model.persona;

public record UpdatePersonaRequest(
    String firstName,
    String lastName,
    String nationalId,
    String phone
) {}
