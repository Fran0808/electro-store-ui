package com.store.inventario.model.empleado;

import com.store.inventario.model.persona.Persona;

import java.math.BigDecimal;

public class Empleado {

// datos que se requieren para post: code, person, position and salary.
    private String code;
    private Persona person;
    private String position;
    private BigDecimal salary;

    public Empleado() {}

    public Empleado(String code, Persona person, String position, BigDecimal salary) {
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }
}
