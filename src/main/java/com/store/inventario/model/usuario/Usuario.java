package com.store.inventario.model.usuario;

public class Usuario {
    private String code;
    private String username;
    private String password;
    private String role;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private boolean status;

    public Usuario() {
    }

    public Usuario(String code, String username, String password, String role, String employeeCode, String firstName, String lastName) {
        this.code = code;
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = true;
    }

    public Usuario(String code, String username, String password, String role, String employeeCode, String firstName, String lastName, boolean status) {
        this.code = code;
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
