package com.store.inventario.controller.productos;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.categoria.Categoria;
import com.store.inventario.controller.categorias.CategoriaFormModalController;
import com.store.inventario.model.producto.CreateProductRequest;
import com.store.inventario.model.producto.Producto;
import com.store.inventario.model.producto.UpdateProductRequest;
import com.store.inventario.service.categoria.CategoriaService;
import com.store.inventario.service.producto.ProductoService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    private final CategoriaService categoriaService = new CategoriaService();
    private final ProductoService productoService = new ProductoService();

    private Producto productoEditar;
    private boolean modoEdicion = false;

    @FXML
    public void initialize() {
        cargarCategorias();
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
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            Integer garantia = Integer.parseInt(txtGarantia.getText().trim());

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

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Formato Inválido");
            alert.setContentText("Verifique que Precio y Garantía sean valores numéricos válidos.");
            alert.showAndWait();
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