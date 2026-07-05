package com.store.inventario.component;

import com.store.inventario.module.product.model.entity.Category;
import com.store.inventario.module.product.model.entity.Product;
import com.store.inventario.module.product.request.UpdateProductRequest;
import com.store.inventario.module.product.service.CategoryService;
import com.store.inventario.module.product.service.ProductService;
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

    private Product product;
    private final CategoryService categoryService = new CategoryService();
    private final ProductService productService = new ProductService();

    @FXML
    public void initialize() {
        spnLimite.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, AlertaController.globalAlertLimit));
    }

    public void setProducto(Product product) {
        this.product = product;
        if (product != null) {
            int currentLimit = product.getLowStock() != null ? product.getLowStock() : AlertaController.globalAlertLimit;
            spnLimite.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, currentLimit));
        }
    }

    @FXML
    private void handleGuardar() {
        if (product == null) {
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
                categoryCode = categoryService.obtenerCategorias().getContent().stream()
                        .filter(c -> c.getName().equals(product.getCategoryName()))
                        .map(Category::getCode)
                        .findFirst()
                        .orElse("");
            } catch (Exception ex) {
                System.err.println("No se pudo obtener el código de categoría: " + ex.getMessage());
            }

            UpdateProductRequest updateRequest = new UpdateProductRequest(
                    categoryCode,
                    product.getName(),
                    product.getBrand() != null ? product.getBrand() : "",
                    product.getModel() != null ? product.getModel() : "",
                    product.getSalePrice(),
                    product.getDescription() != null ? product.getDescription() : "",
                    product.getWarrantyMonths() != null ? product.getWarrantyMonths() : 0,
                    limite
            );

            productService.actualizarProducto(product.getCode(), updateRequest);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Guardado", "Límite para " + product.getName() + " actualizado a: " + limite);

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
        com.store.inventario.shared.utils.WindowUtils.applyIcon(alert);
        alert.showAndWait();
    }
}
