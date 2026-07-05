package com.store.inventario.module.movement.model.entity;

public class RowDetail {
    private final String codigo;
    private final String nombre;
    private final int stockActual;
    private int cantidad;

    public RowDetail(String codigo, String nombre, int stockActual, int cantidad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.stockActual = stockActual;
        this.cantidad = cantidad;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getStockActual() {
        return stockActual;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
