package com.store.inventario.module.person.model.entity;

public class Customer {
    private Person person;
    private String code;
    private String taxId;

    public Customer(){}

    public Customer(Person person, String code, String taxId) {
        this.person = person;
        this.code = code;
        this.taxId = taxId;
    }

    public Person getPerson() {return person;}

    public void setPerson(Person person) {this.person = person;}

    public String getCode() {return code;}

    public void setCode(String code) {this.code = code;}

    public String getTaxId() {return taxId;}

    public void setTaxId(String taxId) {this.taxId = taxId;}
}
