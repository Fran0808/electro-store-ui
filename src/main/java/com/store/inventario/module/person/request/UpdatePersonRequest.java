package com.store.inventario.module.person.request;

public record UpdatePersonRequest(
    String firstName,
    String lastName,
    String nationalId,
    String phone
) {}
