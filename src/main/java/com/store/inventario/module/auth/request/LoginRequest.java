package com.store.inventario.module.auth.request;

public record LoginRequest(
        String username,
        String password
) {
}
