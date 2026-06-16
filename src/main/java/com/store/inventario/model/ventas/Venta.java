package com.store.inventario.model.ventas;

import com.store.inventario.model.clientes.Cliente;
import com.store.inventario.model.usuario.Usuario;
import java.util.List;

public class Venta {
    private String code;
    private Usuario user;
    private Cliente customer;
    private String saleDate;
    private List<VentaDetalle> details;

    public Venta() {}

    public Venta(String code, Usuario user, Cliente customer, String saleDate, List<VentaDetalle> details) {
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

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public Cliente getCustomer() {
        return customer;
    }

    public void setCustomer(Cliente customer) {
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
