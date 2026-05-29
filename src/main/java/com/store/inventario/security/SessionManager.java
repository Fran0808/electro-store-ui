package com.store.inventario.security;

public class SessionManager {
    private static SessionManager instance;
    private String token;
    private String username;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void guardarSesion(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public void cerrarSesion() {
        this.token = null;
        this.username = null;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public boolean isAutenticado() { return token != null; }
}
