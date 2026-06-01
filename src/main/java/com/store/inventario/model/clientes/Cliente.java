package com.store.inventario.model.clientes;

import com.store.inventario.model.persona.Persona;

public class Cliente {
    private Persona person;
    private String code;
    private String taxId;

    public Cliente(){}

    public Cliente(Persona person, String code, String taxId) {
        this.person = person;
        this.code = code;
        this.taxId = taxId;
    }

    public Persona getPerson() {return person;}

    public void setPerson(Persona person) {this.person = person;}

    public String getCode() {return code;}

    public void setCode(String code) {this.code = code;}

    public String getTaxId() {return taxId;}

    public void setTaxId(String taxId) {this.taxId = taxId;}
}
