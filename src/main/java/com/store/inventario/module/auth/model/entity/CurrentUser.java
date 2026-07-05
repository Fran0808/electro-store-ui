package com.store.inventario.module.auth.model.entity;

public class CurrentUser extends UserIdentity {

    public CurrentUser(String code, String username, String firstName, String lastName, String role) {
        super(code, username, firstName, lastName, role);
    }

}
