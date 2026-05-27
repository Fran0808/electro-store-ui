module com.store.inventario {
    requires javafx.controls;
    requires javafx.fxml;


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
    //opens com.store.inventario.controller.clientes to javafx.fxml;

}