package com.store.inventario.module.product.controller;

import com.store.inventario.module.product.model.entity.Category;
import com.store.inventario.module.product.service.CategoryService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CategoryFormModalController {

    @FXML
    private TextField txtNombre;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelar;

    private final CategoryService categoryService = new CategoryService();
    private Category categoryCreada = null;
    private boolean guardado = false;

    @FXML
    private void handleGuardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Campo Obligatorio");
            alert.setContentText("Por favor, ingrese el nombre de la categoría.");
            com.store.inventario.shared.utils.WindowUtils.applyIcon(alert);
            alert.showAndWait();
            return;
        }

        try {
            Category nueva = new Category(null, nombre);
            categoryCreada = categoryService.crearCategoria(nueva);
            guardado = true;

            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo crear la categoría");
            alert.setContentText("Ocurrió un error al guardar: " + e.getMessage());
            com.store.inventario.shared.utils.WindowUtils.applyIcon(alert);
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    public Category getCategoriaCreada() {
        return categoryCreada;
    }

    public String getCategoriaNombre() {
        return categoryCreada != null ? categoryCreada.getName() : "";
    }

    public boolean isGuardado() {
        return guardado;
    }
}
