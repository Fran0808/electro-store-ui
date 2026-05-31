package com.store.inventario.controller.proveedor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ProveedorFormController {

    @FXML
    private Button btnCancelar;

    @FXML
    private TextField txtRuc;

    @FXML
    private TextField txtTelefono;

    @FXML
    private void initialize() {
        com.store.inventario.utils.ValidationUtils.hacerSoloNumericoConLimite(txtRuc, 11);
        com.store.inventario.utils.ValidationUtils.hacerSoloTelefono(txtTelefono);
    }

    @FXML
    private void cerrarModal(){
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
