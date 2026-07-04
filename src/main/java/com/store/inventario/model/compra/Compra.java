package com.store.inventario.model.compra;

import com.store.inventario.model.proveedor.Proveedor;
import com.store.inventario.module.auth.model.entity.User;

import java.util.List;

public class Compra {
    private String code;
    private User user;
    private Proveedor supplier;
    private String purchaseDate;
    private List<CompraDetalle> details;

    public Compra() {}

    public Compra(String code, User user, Proveedor supplier, String purchaseDate, List<CompraDetalle> details) {
        this.code = code;
        this.user = user;
        this.supplier = supplier;
        this.purchaseDate = purchaseDate;
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

    public Proveedor getSupplier() {
        return supplier;
    }

    public void setSupplier(Proveedor supplier) {
        this.supplier = supplier;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public List<CompraDetalle> getDetails() {
        return details;
    }

    public void setDetails(List<CompraDetalle> details) {
        this.details = details;
    }
}
