package com.store.inventario.module.person.model.entity;

import com.store.inventario.module.person.model.enums.EmployeePosition;

import java.math.BigDecimal;

public class Employee {

    private String code;
    private Person person;
    private EmployeePosition position;
    private BigDecimal salary;

    public Employee() {}

    public Employee(String code, Person person, EmployeePosition position, BigDecimal salary) {
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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
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
