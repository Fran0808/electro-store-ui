package com.store.inventario.model.clientes;

import com.store.inventario.model.persona.CreatePersonaRequest;

public record CreateClienteRequest(CreatePersonaRequest person, String taxId) {}
