package com.store.inventario.model.clientes;

import com.store.inventario.model.persona.UpdatePersonaRequest;

public class UpdateClienteRequest {
    private UpdatePersonaRequest person;
    private String taxId;

    public UpdateClienteRequest() {
    }

    public UpdateClienteRequest(UpdatePersonaRequest person, String taxId) {
        this.person = person;
        this.taxId = taxId;
    }

    public UpdatePersonaRequest getPerson() {
        return person;
    }

    public void setPerson(UpdatePersonaRequest person) {
        this.person = person;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }
}
