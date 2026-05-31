package com.store.inventario.model.usuario;

public class Usuario {
    private String code;
    private String username;
    private String password;
    private String role;
    private String employeeCode;
    private String firstName;
    private String lastName;

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

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
