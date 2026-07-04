package com.store.inventario.module.person.request;

import com.store.inventario.module.person.model.enums.EmployeePosition;

import java.math.BigDecimal;

public record CreateEmployeeRequest(CreatePersonRequest person,
                                    EmployeePosition position,
                                    BigDecimal salary) {}