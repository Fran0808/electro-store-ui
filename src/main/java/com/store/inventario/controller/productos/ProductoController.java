package com.store.inventario.controller.productos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;

public class ProductoController {
    @FXML
    private Button btnNuevoProducto;

    @FXML
    private void handleForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/productos/producto-form.fxml"));
        Parent root = loader.load();

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Nuevo Producto");
        modal.setScene(new Scene(root));
        modal.showAndWait();
    }
}
