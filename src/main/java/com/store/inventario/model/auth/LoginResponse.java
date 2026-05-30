package com.store.inventario.model.auth;

public class LoginResponse {
    private String token;
    private AuthResponse auth;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public AuthResponse getAuth() { return auth; }
    public void setAuth(AuthResponse auth) { this.auth = auth; }
}
