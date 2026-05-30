package com.store.inventario.model.empleado;

import com.store.inventario.model.persona.CreatePersonaRequest;

import java.math.BigDecimal;

public record CreateEmployeeRequest(CreatePersonaRequest person, String position, BigDecimal salary) {
}