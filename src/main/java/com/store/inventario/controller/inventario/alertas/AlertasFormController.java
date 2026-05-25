package com.store.inventario.controller.inventario.alertas;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

public class AlertasFormController {
    @FXML
    private Spinner<Integer> spnLimite;
    @FXML
    private Button btnCancelar;

    @FXML
    public void initialize() {
        spnLimite.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 20));
    }

    @FXML
    private void handleGuardar() {
        int limite = spnLimite.getValue();
        if (limite < 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido", "El límite no puede ser negativo.");
            return;
        }

        mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado", "Límite actualizado a: " + limite);

        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
