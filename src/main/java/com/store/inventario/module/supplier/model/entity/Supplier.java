package com.store.inventario.module.supplier.model.entity;

public class Supplier {
    private String code;
    private String taxId;
    private String tradeName;
    private String phone;
    private String legalName;

    public Supplier(){
    }

    public Supplier(String code, String taxId, String tradeName, String phone, String legalName){
        this.code = code;
        this.taxId = taxId;
        this.tradeName = tradeName;
        this.phone = phone;
        this.legalName = legalName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }
}
