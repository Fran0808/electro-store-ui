package com.store.inventario.module.person.request;

public record CreatePersonRequest(
    String firstName,
    String lastName,
    String nationalId,
    String phone
) {}
