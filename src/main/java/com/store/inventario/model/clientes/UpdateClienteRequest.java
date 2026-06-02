package com.store.inventario.model.clientes;

import com.store.inventario.model.persona.UpdatePersonaRequest;

public record UpdateClienteRequest(UpdatePersonaRequest person, String taxId) {}
