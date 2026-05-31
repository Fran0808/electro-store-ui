package com.store.inventario.model.empleado;

import com.store.inventario.model.persona.Persona;
import java.math.BigDecimal;

public class Empleado {

    private String code;
    private Persona person;
    private EmployeePosition position;
    private BigDecimal salary;

    public Empleado() {}

    public Empleado(String code, Persona person, EmployeePosition position, BigDecimal salary) {
        this.code = code;
        this.person = person;
        this.position = position;
        this.salary = salary;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Persona getPerson() {
        return person;
    }

    public void setPerson(Persona person) {
        this.person = person;
    }

    public EmployeePosition getPosition() {
        return position;
    }

    public void setPosition(EmployeePosition position) {
        this.position = position;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
}
