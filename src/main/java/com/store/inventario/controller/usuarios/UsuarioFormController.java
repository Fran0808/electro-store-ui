package com.store.inventario.controller.usuarios;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class UsuarioFormController {
    @FXML
    private Button btnCancelar;

    @FXML
    private void cerrarModal(){
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
