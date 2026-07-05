package com.store.inventario.module.auth.response;

public class AuthResponse {

    private String code;
    private String username;
    private String firstName;
    private String lastName;
    private String role;

    public AuthResponse() {}

    public AuthResponse(String code, String username, String firstName, String lastName, String role) {
        this.code = code;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
