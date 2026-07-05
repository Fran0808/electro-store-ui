package com.store.inventario.module.person.request;

public record UpdateCustomerRequest(UpdatePersonRequest person, String taxId) {}
