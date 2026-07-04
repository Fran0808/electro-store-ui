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
    opens com.store.inventario.controller.proveedor;

    opens com.store.inventario.controller.categorias;

    opens com.store.inventario.model;
    opens com.store.inventario.model.producto;
    opens com.store.inventario.model.categoria;
    opens com.store.inventario.model.proveedor;
    opens com.store.inventario.model.guia;
    opens com.store.inventario.model.ventas;
    opens com.store.inventario.model.compra;

    //AUTH
    opens com.store.inventario.module.auth.controller;
    opens com.store.inventario.module.auth.response;
    opens com.store.inventario.module.auth.request;
    opens com.store.inventario.module.auth.model.entity;
    //PERSON
    opens com.store.inventario.module.person.controller;
    opens com.store.inventario.module.person.model.entity;
    opens com.store.inventario.module.person.request;
    opens com.store.inventario.module.person.model.enums;

}