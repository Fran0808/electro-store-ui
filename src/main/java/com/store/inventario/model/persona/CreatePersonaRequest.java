package com.store.inventario.model.persona;

public record CreatePersonaRequest(
    String firstName,
    String lastName,
    String nationalId,
    String phone
) {}
