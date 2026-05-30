package com.store.inventario.model.categoria;

public class Categoria {
    private String code;
    private String name;

    public Categoria() {
    }

    public Categoria(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Categoria that = (Categoria) o;
        return name.equals(that.name);
    }
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
