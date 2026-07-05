package com.store.inventario.module.person.request;

public record CreateCustomerRequest(CreatePersonRequest person, String taxId) {}
