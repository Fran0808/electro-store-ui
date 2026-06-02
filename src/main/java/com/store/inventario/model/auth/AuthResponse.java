package com.store.inventario.model.auth;

public record AuthResponse(
    String code,
    String username,
    String firstName,
    String lastName,
    String role
) {}
