package com.store.inventario.model.clientes;

import com.store.inventario.model.persona.CreatePersonaRequest;

public class CreateClienteRequest {
    private CreatePersonaRequest person;
    private String taxId;

    public CreateClienteRequest() {
    }

    public CreateClienteRequest(CreatePersonaRequest person, String taxId) {
        this.person = person;
        this.taxId = taxId;
    }

    public CreatePersonaRequest getPerson() {
        return person;
    }

    public void setPerson(CreatePersonaRequest person) {
        this.person = person;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }
}
