package com.store.inventario.controller.productos;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ProductoFormController {

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtNombre;

    @FXML
    private ComboBox<String> cbCategoria;

    @FXML
    private TextField txtMarca;

    @FXML
    private TextField txtModelo;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtStock;

    @FXML
    private TextField txtGarantia;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelar;

    @FXML
    public void initialize() {
        if (cbCategoria != null) {
            cbCategoria.getItems().addAll(
                    "Línea Blanca",
                    "Electrónica",
                    "Cómputo",
                    "Celulares",
                    "Videojuegos",
                    "Audio y Video",
                    "Seguridad"
            );
        }
    }

    @FXML
    private void handleGuardar() {
        if (txtCodigo.getText().isEmpty() || txtNombre.getText().isEmpty() || txtPrecio.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Campos Incompletos");
            alert.setContentText("Por favor complete los campos obligatorios (Código, Nombre y Precio)");
            alert.showAndWait();
            return;
        }

        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleNuevaCategoria() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/store/inventario/views/productos/categoria-form-modal.fxml"));
            javafx.scene.Parent root = loader.load();

            Stage modal = new Stage();
            modal.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            modal.setTitle("Nueva Categoría");
            modal.setScene(new javafx.scene.Scene(root));

            CategoriaFormModalController controller = loader.getController();
            modal.showAndWait();

            if (controller.isGuardado()) {
                String cat = controller.getCategoriaNombre().trim();
                if (!cat.isEmpty()) {
                    if (!cbCategoria.getItems().contains(cat)) {
                        cbCategoria.getItems().add(cat);
                        cbCategoria.setValue(cat);
                    } else {
                        cbCategoria.setValue(cat);
                    }
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo cargar la vista");
            alert.setContentText("Ocurrió un error al intentar abrir el formulario de categorías.");
            alert.showAndWait();
        }
    }
}
