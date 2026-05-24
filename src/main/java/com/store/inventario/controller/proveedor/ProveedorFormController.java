package com.store.inventario.controller.proveedor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ProveedorFormController {

    @FXML
    private Button btnCancelar;

    @FXML
    private void cerrarModal(){
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
