module com.store.inventario {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.store.inventario to javafx.fxml;
    exports com.store.inventario;
}