package com.store.inventario.security;

public class SessionManager {
    private static SessionManager instance;
    private String token;
    private String username;
    private String role;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void guardarSesion(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public void cerrarSesion() {
        this.token = null;
        this.username = null;
        this.role = null;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public boolean isAutenticado() { return token != null; }
}