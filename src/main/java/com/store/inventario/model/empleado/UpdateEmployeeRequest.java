package com.store.inventario.model.empleado;

import com.store.inventario.model.persona.UpdatePersonaRequest;
import java.math.BigDecimal;

public record UpdateEmployeeRequest (UpdatePersonaRequest person,
                                     String position,
                                     BigDecimal salary){
}
