package com.store.inventario.controller.inventario.guias;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GuiaFormController {
    @FXML
    private TextField txtCodigo, txtMotivo, txtUsuario, txtStockActual;
    @FXML
    private RadioButton rbEntry, rbExit;
    @FXML
    private TextField txtDescripcion;
    @FXML
    private ComboBox<String> cbProducto;
    @FXML
    private Spinner<Integer> spnCantidad;
    @FXML
    private Button btnCancelar, btnGuardar, btnAgregarProducto;

    @FXML
    public void initialize() {
        txtCodigo.setText("GU-" + String.format("%06d", (int)(Math.random() * 1000000)));
        txtUsuario.setText("Usuario Actual");
        
        cbProducto.getItems().addAll(
            "Cable HDMI 2.1 4K 2m",
            "Teclado Mecánico RGB TKL",
            "Mouse Inalámbrico Pro",
            "Monitor 27\" 4K IPS",
            "Laptop Business Pro 14\""
        );
        
        spnCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 1));
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleGuardar() {
        String motivo = txtMotivo.getText();
        if (motivo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido", "Ingrese un motivo");
            return;
        }
        
        String tipo = rbEntry.isSelected() ? "ENTRY" : "EXIT";
        mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado", "Guía " + txtCodigo.getText() + " (" + tipo + ") registrada");
        
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleAgregarProducto() {
        if (cbProducto.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo requerido", "Seleccione un producto");
            return;
        }
        if (spnCantidad.getValue() <= 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido", "Ingrese una cantidad mayor a 0");
            return;
        }
        
        mostrarAlerta(Alert.AlertType.INFORMATION, "Producto agregado", 
            cbProducto.getValue() + " - Cantidad: " + spnCantidad.getValue());
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
