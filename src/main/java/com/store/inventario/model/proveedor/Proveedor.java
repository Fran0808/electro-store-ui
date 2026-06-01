package com.store.inventario.model.proveedor;

public class Proveedor {
    private String code;
    private String ruc;
    private String tradeName;
    private String phone;
    private String companyName;

    public Proveedor (){
    }

    public Proveedor(String code, String ruc, String tradeName, String phone, String companyName){
        this.code = code;
        this.ruc = ruc;
        this.tradeName = tradeName;
        this.phone = phone;
        this.companyName = companyName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
