package com.store.inventario.module.auth.model.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class UserIdentity {

    protected String code;
    protected String username;
    protected String firstName;
    protected String lastName;
    protected String role;

    public String getFullName() {
        return firstName + " " + lastName;
    }

}
