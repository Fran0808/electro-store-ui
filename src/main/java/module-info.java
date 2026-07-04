module com.store.inventario {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;
    requires java.desktop;
    requires java.prefs;

    exports com.store.inventario;

    opens com.store.inventario;
    opens com.store.inventario.controller;
    opens com.store.inventario.controller.ventas;
    opens com.store.inventario.controller.productos;
    opens com.store.inventario.controller.alertas;
    opens com.store.inventario.controller.guias;
    opens com.store.inventario.controller.compra;
    opens com.store.inventario.controller.empleados;
    opens com.store.inventario.controller.proveedor;
    opens com.store.inventario.controller.clientes;

    opens com.store.inventario.controller.categorias;

    opens com.store.inventario.model;
    opens com.store.inventario.model.producto;
    opens com.store.inventario.model.categoria;
    opens com.store.inventario.model.persona;
    opens com.store.inventario.model.clientes;
    opens com.store.inventario.model.proveedor;
    opens com.store.inventario.model.guia;
    opens com.store.inventario.model.ventas;
    opens com.store.inventario.model.compra;
    opens com.store.inventario.model.empleado;

    //AUTH
    opens com.store.inventario.module.auth.controller;
    opens com.store.inventario.module.auth.response;
    opens com.store.inventario.module.auth.request;
    opens com.store.inventario.module.auth.model.entity;

}