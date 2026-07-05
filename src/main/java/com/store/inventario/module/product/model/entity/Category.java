package com.store.inventario.module.product.model.entity;

public class Category {
    private String code;
    private String name;

    public Category() {
    }

    public Category(String code, String name) {
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
        Category that = (Category) o;
        return name.equals(that.name);
    }
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
