package com.store.inventario.security;

import java.util.prefs.Preferences;

public class SessionManager {
    private static SessionManager instance;
    private String token;
    private String username;
    private String role;

    private SessionManager() {
        cargarSesionGuardada();
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    private void cargarSesionGuardada() {
        try {
            Preferences prefs = Preferences.userNodeForPackage(SessionManager.class);
            this.token = prefs.get("token", null);
            this.username = prefs.get("username", null);
            this.role = prefs.get("role", null);
        } catch (Exception e) {
            System.err.println("No se pudo cargar la sesión persistida: " + e.getMessage());
        }
    }

    public void guardarSesion(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public void guardarSesion(String token, String username, String role, boolean recordar) {
        this.token = token;
        this.username = username;
        this.role = role;

        if (recordar) {
            try {
                Preferences prefs = Preferences.userNodeForPackage(SessionManager.class);
                prefs.put("token", token);
                prefs.put("username", username);
                prefs.put("role", role);
                prefs.flush();
            } catch (Exception e) {
                System.err.println("No se pudo persistir la sesión: " + e.getMessage());
            }
        }
    }

    public void cerrarSesion() {
        this.token = null;
        this.username = null;
        this.role = null;
        try {
            Preferences prefs = Preferences.userNodeForPackage(SessionManager.class);
            prefs.remove("token");
            prefs.remove("username");
            prefs.remove("role");
            prefs.flush();
        } catch (Exception e) {
            System.err.println("No se pudo limpiar la sesión persistida: " + e.getMessage());
        }
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public boolean isAutenticado() { return token != null; }
}