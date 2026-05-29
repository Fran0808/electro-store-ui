module com.store.inventario {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;


    exports com.store.inventario;
    opens com.store.inventario to javafx.fxml;
    opens com.store.inventario.controller.login to javafx.fxml;
    opens com.store.inventario.controller.ventas to javafx.fxml;
    opens com.store.inventario.controller.productos to javafx.fxml;
    opens com.store.inventario.controller.inventario.alertas to javafx.fxml;
    opens com.store.inventario.controller.inventario.guias to javafx.fxml;
    opens com.store.inventario.controller.inventario.movimientos to javafx.fxml;
    opens com.store.inventario.controller.compra to javafx.fxml;
    opens com.store.inventario.controller.empleados to javafx.fxml;
    opens com.store.inventario.controller.proveedor to javafx.fxml;
    opens com.store.inventario.controller to javafx.fxml;
    opens com.store.inventario.model.auth to com.google.gson;
    opens com.store.inventario.model.producto to com.google.gson;
    opens com.store.inventario.model to com.google.gson;
    opens com.store.inventario.model.categoria to com.google.gson;


}