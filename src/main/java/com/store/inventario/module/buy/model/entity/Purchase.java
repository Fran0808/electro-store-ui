package com.store.inventario.module.buy.model.entity;

import com.store.inventario.module.supplier.model.entity.Supplier;
import com.store.inventario.module.auth.model.entity.User;

import java.util.List;

public class Purchase {
    private String code;
    private User user;
    private Supplier supplier;
    private String purchaseDate;
    private List<PurchaseDetail> details;

    public Purchase() {}

    public Purchase(String code, User user, Supplier supplier, String purchaseDate, List<PurchaseDetail> details) {
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

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public List<PurchaseDetail> getDetails() {
        return details;
    }

    public void setDetails(List<PurchaseDetail> details) {
        this.details = details;
    }
}
