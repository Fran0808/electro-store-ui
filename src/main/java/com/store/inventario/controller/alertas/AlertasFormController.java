package com.store.inventario.controller.alertas;

import com.store.inventario.model.producto.Producto;
import com.store.inventario.model.producto.UpdateProductRequest;
import com.store.inventario.service.categoria.CategoriaService;
import com.store.inventario.service.producto.ProductoService;
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

    private Producto producto;
    private final CategoriaService categoriaService = new CategoriaService();
    private final ProductoService productoService = new ProductoService();

    @FXML
    public void initialize() {
        spnLimite.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, AlertaController.globalAlertLimit));
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        if (producto != null) {
            int currentLimit = producto.getLowStock() != null ? producto.getLowStock() : AlertaController.globalAlertLimit;
            spnLimite.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, currentLimit));
        }
    }

    @FXML
    private void handleGuardar() {
        if (producto == null) {
            return;
        }

        int limite = spnLimite.getValue();
        if (limite < 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Valor inválido", "El límite no puede ser negativo.");
            return;
        }

        try {
            String categoryCode = "";
            try {
                categoryCode = categoriaService.obtenerCategorias().getContent().stream()
                        .filter(c -> c.getName().equals(producto.getCategoryName()))
                        .map(com.store.inventario.model.categoria.Categoria::getCode)
                        .findFirst()
                        .orElse("");
            } catch (Exception ex) {
                System.err.println("No se pudo obtener el código de categoría: " + ex.getMessage());
            }

            UpdateProductRequest updateRequest = new UpdateProductRequest(
                    categoryCode,
                    producto.getName(),
                    producto.getBrand() != null ? producto.getBrand() : "",
                    producto.getModel() != null ? producto.getModel() : "",
                    producto.getSalePrice(),
                    producto.getDescription() != null ? producto.getDescription() : "",
                    producto.getWarrantyMonths() != null ? producto.getWarrantyMonths() : 0,
                    limite
            );

            productoService.actualizarProducto(producto.getCode(), updateRequest);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado", "Límite para " + producto.getName() + " actualizado a: " + limite);

            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el límite del producto: " + e.getMessage());
        }
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
        com.store.inventario.utils.WindowUtils.applyIcon(alert);
        alert.showAndWait();
    }
}
