package com.store.inventario.controller.productos;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.categoria.Categoria;
import com.store.inventario.controller.categorias.CategoriaFormModalController;
import com.store.inventario.model.producto.CreateProductRequest;
import com.store.inventario.model.producto.Producto;
import com.store.inventario.model.producto.UpdateProductRequest;
import com.store.inventario.security.SessionManager;
import com.store.inventario.service.categoria.CategoriaService;
import com.store.inventario.service.producto.ProductoService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class ProductoFormController {

    @FXML
    private Label lblTitulo;

    @FXML
    private Label lblSubtitulo;

    @FXML
    private TextField txtNombre;

    @FXML
    private ComboBox<Categoria> cbCategoria;

    @FXML
    private TextField txtMarca;

    @FXML
    private TextField txtModelo;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtGarantia;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnNuevaCategoria;

    private final CategoriaService categoriaService = new CategoriaService();
    private final ProductoService productoService = new ProductoService();

    private Producto productoEditar;
    private boolean modoEdicion = false;

    @FXML
    public void initialize() {
        cargarCategorias();
        com.store.inventario.utils.ValidationUtils.hacerSoloDecimal(txtPrecio, 8, 2);
        com.store.inventario.utils.ValidationUtils.hacerSoloNumericoConLimite(txtGarantia, 3);
        String roleActual = SessionManager.getInstance().getRole();

        if(!"ADMIN".equalsIgnoreCase(roleActual)){
            btnNuevaCategoria.setVisible(false);
            btnNuevaCategoria.setManaged(false);
        }
    }

    public void setProductoEditar(Producto producto) {
        this.productoEditar = producto;
        this.modoEdicion = true;

        lblTitulo.setText("Editar Producto");
        lblSubtitulo.setText("Modifique la información del producto");
        btnGuardar.setText("Actualizar Producto");

        txtNombre.setText(producto.getName());
        txtMarca.setText(producto.getBrand() != null ? producto.getBrand() : "");
        txtModelo.setText(producto.getModel() != null ? producto.getModel() : "");
        txtPrecio.setText(producto.getSalePrice() != null ? producto.getSalePrice().toString() : "");
        txtGarantia.setText(producto.getWarrantyMonths() != null ? producto.getWarrantyMonths().toString() : "");
        txtDescripcion.setText(producto.getDescription() != null ? producto.getDescription() : "");

        for (Categoria cat : cbCategoria.getItems()) {
            if (cat.getName().equals(producto.getCategoryName())) {
                cbCategoria.setValue(cat);
                break;
            }
        }
    }

    @FXML
    private void handleGuardar() {

        if (txtNombre.getText().trim().isEmpty() ||
            cbCategoria.getValue() == null ||
            txtPrecio.getText().trim().isEmpty() ||
            txtGarantia.getText().trim().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Campos Incompletos");
            alert.setContentText("Por favor complete los campos obligatorios (*).");
            alert.showAndWait();
            return;
        }

        try {
            BigDecimal precio;
            try {
                precio = new BigDecimal(txtPrecio.getText().trim());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText("Precio Inválido");
                alert.setContentText("El precio de venta ingresado no es válido.");
                alert.showAndWait();
                return;
            }

            if (precio.compareTo(BigDecimal.ZERO) <= 0 || precio.compareTo(new BigDecimal("10000000")) > 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText("Precio Fuera de Rango");
                alert.setContentText("El precio de venta debe ser mayor a 0 y no debe superar los 10,000,000.");
                alert.showAndWait();
                return;
            }

            Long garantiaLong;
            try {
                garantiaLong = Long.parseLong(txtGarantia.getText().trim());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText("Garantía Inválida");
                alert.setContentText("El valor de garantía es demasiado grande o no es válido.");
                alert.showAndWait();
                return;
            }

            if (garantiaLong < 0 || garantiaLong > 240) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Advertencia");
                alert.setHeaderText("Garantía Fuera de Rango");
                alert.setContentText("Los meses de garantía deben estar entre 0 y 240 meses (máximo 20 años).");
                alert.showAndWait();
                return;
            }
            Integer garantia = garantiaLong.intValue();

            if (modoEdicion) {
                UpdateProductRequest updateRequest = new UpdateProductRequest(
                        cbCategoria.getValue().getCode(),
                        txtNombre.getText().trim(),
                        txtMarca.getText().trim(),
                        txtModelo.getText().trim(),
                        precio,
                        txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "",
                        garantia
                );

                productoService.actualizarProducto(productoEditar.getCode(), updateRequest);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText("Producto Actualizado");
                alert.setContentText("El producto se ha actualizado correctamente.");
                alert.showAndWait();
            } else {
                CreateProductRequest createRequest = new CreateProductRequest(
                        cbCategoria.getValue().getCode(),
                        txtNombre.getText().trim(),
                        txtMarca.getText().trim(),
                        txtModelo.getText().trim(),
                        precio,
                        txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "",
                        garantia
                );

                productoService.crearProducto(createRequest);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText("Producto Registrado");
                alert.setContentText("El producto se ha creado correctamente.");
                alert.showAndWait();
            }

            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(modoEdicion ? "No se pudo actualizar el producto" : "No se pudo crear el producto");
            alert.setContentText("Ocurrió un error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleNuevaCategoria() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/productos/categoria-form-modal.fxml"));
            Parent root = loader.load();
            Stage modal = new Stage();

            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Nueva Categoría");
            modal.setScene(new Scene(root));

            CategoriaFormModalController controller = loader.getController();

            modal.showAndWait();

            if (controller.isGuardado()) {
                Categoria categoria = controller.getCategoriaCreada();

                if (categoria != null) {
                    if (!cbCategoria.getItems().contains(categoria)) {
                        cbCategoria.getItems().add(categoria);
                    }

                    cbCategoria.setValue(categoria);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo cargar la vista");
            alert.setContentText("Ocurrió un error al abrir el formulario.");
            alert.showAndWait();
        }
    }

    private void cargarCategorias() {
        try {
            PageResponse<Categoria> response = categoriaService.obtenerCategorias();
            cbCategoria.setItems(FXCollections.observableArrayList(response.getContent()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}