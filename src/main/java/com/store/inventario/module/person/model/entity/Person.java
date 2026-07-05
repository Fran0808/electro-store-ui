package com.store.inventario.module.person.model.entity;

public class Person {
    private String code;
    private String firstName;
    private String lastName;
    private String nationalId;
    private String phone;

    public Person() {
    }

    public Person(String code, String firstName, String lastName, String nationalId, String phone) {
        this.code = code;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalId = nationalId;
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
