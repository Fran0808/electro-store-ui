package com.store.inventario.model.ventas;

import com.store.inventario.module.person.model.entity.Customer;
import com.store.inventario.module.auth.model.entity.User;
import java.util.List;

public class Venta {
    private String code;
    private User user;
    private Customer customer;
    private String saleDate;
    private List<VentaDetalle> details;

    public Venta() {}

    public Venta(String code, User user, Customer customer, String saleDate, List<VentaDetalle> details) {
        this.code = code;
        this.user = user;
        this.customer = customer;
        this.saleDate = saleDate;
        this.details = details;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public List<VentaDetalle> getDetails() {
        return details;
    }

    public void setDetails(List<VentaDetalle> details) {
        this.details = details;
    }
}
