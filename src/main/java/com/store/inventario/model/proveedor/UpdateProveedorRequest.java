package com.store.inventario.model.proveedor;

public class UpdateProveedorRequest {

    private String taxId;
    private String tradeName;
    private String phone;
    private String legalName;

    public UpdateProveedorRequest() {
    }

    public UpdateProveedorRequest(String taxId, String tradeName, String phone, String legalName) {
        this.taxId = taxId;
        this.tradeName = tradeName;
        this.phone = phone;
        this.legalName = legalName;
    }

    public String getTaxId() {
        return taxId;
    }

    public String getTradeName() {
        return tradeName;
    }

    public String getPhone() {
        return phone;
    }

    public String getLegalName() {
        return legalName;
    }
}
