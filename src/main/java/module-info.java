module com.store.inventario {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;
    requires java.desktop;
    requires java.prefs;
    requires static lombok;

    exports com.store.inventario;

    opens com.store.inventario;

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
    //PRODUCT
    opens com.store.inventario.module.product.controller;
    opens com.store.inventario.module.product.model.entity;
    opens com.store.inventario.module.product.request;
    //SUPPLIER
    opens com.store.inventario.module.supplier.controller;
    opens com.store.inventario.module.supplier.model.entity;
    opens com.store.inventario.module.supplier.request;
    opens com.store.inventario.module.supplier.service;
    //MOVEMENT
    opens com.store.inventario.module.movement.model.entity;
    opens com.store.inventario.module.movement.request;
    opens com.store.inventario.module.movement.controller;
    //PURCHASE
    opens com.store.inventario.module.buy.controller;
    opens com.store.inventario.module.buy.model.entity;
    opens com.store.inventario.module.buy.request;
    //SALE
    opens com.store.inventario.module.sale.controller;
    opens com.store.inventario.module.sale.model.entity;
    opens com.store.inventario.module.sale.request;
    opens com.store.inventario.component;
    opens com.store.inventario.shared.model;
    opens com.store.inventario.shared.utils;

}