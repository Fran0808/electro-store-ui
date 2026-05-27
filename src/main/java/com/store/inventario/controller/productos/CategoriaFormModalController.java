package com.store.inventario.controller.productos;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CategoriaFormModalController {

    @FXML
    private TextField txtNombre;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelar;

    private String categoriaNombre = "";
    private boolean guardado = false;

    @FXML
    private void handleGuardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Campo Obligatorio");
            alert.setContentText("Por favor, ingrese el nombre de la categoría.");
            alert.showAndWait();
            return;
        }

        categoriaNombre = nombre;
        guardado = true;

        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public boolean isGuardado() {
        return guardado;
    }
}
