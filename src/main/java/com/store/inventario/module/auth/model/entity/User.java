package com.store.inventario.module.auth.model.entity;

import lombok.Getter;

@Getter
public class User extends UserIdentity {

    private final String password;
    private final String employeeCode;
    private final boolean status = true;

    public User(String code, String username, String password, String role, String employeeCode, String firstName, String lastName) {
        super(code, username, firstName, lastName, role);
        this.password = password;
        this.employeeCode = employeeCode;
    }

}
