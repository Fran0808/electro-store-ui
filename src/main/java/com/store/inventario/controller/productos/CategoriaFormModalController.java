package com.store.inventario.controller.productos;

import com.store.inventario.model.categoria.Categoria;
import com.store.inventario.service.categoria.CategoriaService;
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

    private final CategoriaService categoriaService = new CategoriaService();
    private Categoria categoriaCreada = null;
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

        try {
            Categoria nueva = new Categoria(null, nombre);
            categoriaCreada = categoriaService.crearCategoria(nueva);
            guardado = true;

            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo crear la categoría");
            alert.setContentText("Ocurrió un error al guardar: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    public Categoria getCategoriaCreada() {
        return categoriaCreada;
    }

    public String getCategoriaNombre() {
        return categoriaCreada != null ? categoriaCreada.getName() : "";
    }

    public boolean isGuardado() {
        return guardado;
    }
}
