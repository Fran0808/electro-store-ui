package com.store.inventario.controller.compra;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.compra.CreatePurchaseDetailRequest;
import com.store.inventario.model.compra.CreatePurchaseRequest;
import com.store.inventario.model.producto.Producto;
import com.store.inventario.model.proveedor.Proveedor;
import com.store.inventario.security.SessionManager;
import com.store.inventario.service.compra.CompraService;
import com.store.inventario.service.proveedor.ProveedorService;
import com.store.inventario.service.producto.ProductoService;
import com.store.inventario.controller.proveedor.ProveedorSearchModalController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.geometry.Pos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CompraFormController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtProveedor;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtFecha;

    private Proveedor proveedorSeleccionado = null;
    
    // Controles para el catálogo de productos
    @FXML private TextField txtBuscarProducto;
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCatCodigo;
    @FXML private TableColumn<Producto, String> colCatNombre;
    @FXML private TableColumn<Producto, Integer> colCatStock;
    @FXML private Label lblProductoSeleccionado;
    @FXML private Label lblPrecioVentaRef;
    @FXML private Label lblMargenRef;
    @FXML private TextField txtPrecioCompra;
    @FXML private TextField txtCantidad;

    // Controles para el detalle de la compra
    @FXML private TableView<DetalleTemporal> tblDetalleCompra;
    @FXML private TableColumn<DetalleTemporal, String> colProducto;
    @FXML private TableColumn<DetalleTemporal, BigDecimal> colPrecioCompra;
    @FXML private TableColumn<DetalleTemporal, Integer> colCantidad;
    @FXML private TableColumn<DetalleTemporal, BigDecimal> colSubtotal;
    @FXML private TableColumn<DetalleTemporal, Void> colEliminar;
    @FXML private Label lblTotal;
    @FXML private Button btnCancelar;

    private final ProveedorService proveedorService = new ProveedorService();
    private final ProductoService productoService = new ProductoService();
    private final CompraService compraService = new CompraService();
    
    private final ObservableList<Producto> listaCatalogProductos = FXCollections.observableArrayList();
    private final ObservableList<DetalleTemporal> listaDetalle = FXCollections.observableArrayList();

    public static class DetalleTemporal {
        private final Producto producto;
        private final BigDecimal precioCompra;
        private final int cantidad;

        public DetalleTemporal(Producto producto, BigDecimal precioCompra, int cantidad) {
            this.producto = producto;
            this.precioCompra = precioCompra;
            this.cantidad = cantidad;
        }

        public Producto getProducto() { return producto; }
        public String getNombreProducto() { return producto != null ? producto.getName() : ""; }
        public BigDecimal getPrecioCompra() { return precioCompra; }
        public int getQuantity() { return cantidad; }
        public BigDecimal getSubtotal() {
            return precioCompra != null ? precioCompra.multiply(BigDecimal.valueOf(cantidad)) : BigDecimal.ZERO;
        }
    }

    @FXML
    public void initialize() {
        String user = SessionManager.getInstance().getUsername();
        txtUsuario.setText(user != null && !user.isEmpty()
                ? user.substring(0, 1).toUpperCase() + user.substring(1)
                : "Sesión Activa");
        txtFecha.setText(LocalDate.now().toString());

        colCatCodigo.setCellValueFactory(new PropertyValueFactory<>("code"));
        colCatNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCatStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        
        configurarColumnaEliminar();
        
        tblDetalleCompra.setItems(listaDetalle);
        tblProductos.setItems(listaCatalogProductos);

        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                lblProductoSeleccionado.setText("Producto: " + newSelection.getName() + " (" + newSelection.getCode() + ")");
                txtCantidad.setText("1");
                if (newSelection.getSalePrice() != null) {
                    BigDecimal salePrice = newSelection.getSalePrice();
                    lblPrecioVentaRef.setText("Precio Venta Ref: S/ " + salePrice.setScale(2, RoundingMode.HALF_UP));
                    
                    BigDecimal suggestedPrice = salePrice.multiply(BigDecimal.valueOf(0.7));
                    txtPrecioCompra.setText(suggestedPrice.setScale(2, RoundingMode.HALF_UP).toString());
                    actualizarMargen(suggestedPrice, salePrice);
                } else {
                    lblPrecioVentaRef.setText("Precio Venta Ref: S/ 0.00");
                    txtPrecioCompra.setText("10.00");
                    lblMargenRef.setText("Margen: N/A");
                }
            } else {
                lblProductoSeleccionado.setText("Seleccione un producto del catálogo...");
                lblPrecioVentaRef.setText("");
                lblMargenRef.setText("");
                txtCantidad.clear();
                txtPrecioCompra.clear();
            }
        });

        txtPrecioCompra.textProperty().addListener((obs, oldText, newText) -> {
            Producto selected = tblProductos.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getSalePrice() != null && newText != null && !newText.trim().isEmpty()) {
                try {
                    BigDecimal purchasePrice = new BigDecimal(newText.trim());
                    actualizarMargen(purchasePrice, selected.getSalePrice());
                } catch (Exception e) {
                    lblMargenRef.setText("Margen: Inválido");
                }
            }
        });

        setProveedorSeleccionado(null);
        cargarProductos("");
    }

    private void setProveedorSeleccionado(Proveedor proveedor) {
        this.proveedorSeleccionado = proveedor;
        if (proveedor != null) {
            txtProveedor.setText(proveedor.getCode() + " - " + proveedor.getTradeName());
        } else {
            txtProveedor.setText("Seleccione un proveedor...");
        }
    }

    @FXML
    private void abrirBuscadorProveedor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/proveedores/proveedor-search-modal.fxml"));
            Parent root = loader.load();

            ProveedorSearchModalController controller = loader.getController();

            Stage modal = new Stage();
            com.store.inventario.utils.WindowUtils.applyIcon(modal);
            modal.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            modal.setTitle("Buscar Proveedor");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            Proveedor seleccionado = controller.getProveedorSeleccionado();
            if (seleccionado != null) {
                setProveedorSeleccionado(seleccionado);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el buscador de proveedores: " + e.getMessage());
        }
    }

    private void cargarProductos(String search) {
        new Thread(() -> {
            try {
                PageResponse<Producto> response = productoService.obtenerProductos(search, 0, 100);
                if (response != null && response.getContent() != null) {
                    Platform.runLater(() -> listaCatalogProductos.setAll(response.getContent()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void ejecutarBusquedaLocal() {
        String query = txtBuscarProducto.getText();
        cargarProductos(query != null ? query.trim() : "");
    }

    @FXML
    private void agregarProductoALaLista() {
        Producto selected = tblProductos.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostrarAlerta("Campos requeridos", "Debe seleccionar un producto del catálogo de la izquierda.");
            return;
        }

        String cantStr = txtCantidad.getText();
        int cantidad;
        try {
            cantidad = Integer.parseInt(cantStr.trim());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Cantidad inválida. Debe ser un número entero mayor a 0.");
            return;
        }

        String precioStr = txtPrecioCompra.getText();
        BigDecimal precio;
        try {
            precio = new BigDecimal(precioStr.trim());
            if (precio.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Precio unitario de compra inválido. Debe ser un número mayor a 0.");
            return;
        }


        int index = -1;
        for (int i = 0; i < listaDetalle.size(); i++) {
            if (listaDetalle.get(i).getProducto().getCode().equals(selected.getCode())) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            int nuevaCantidad = listaDetalle.get(index).getQuantity() + cantidad;
            listaDetalle.set(index, new DetalleTemporal(selected, precio, nuevaCantidad));
        } else {
            listaDetalle.add(new DetalleTemporal(selected, precio, cantidad));
        }

        actualizarTotal();
        tblProductos.getSelectionModel().clearSelection();
    }

    private void configurarColumnaEliminar() {
        colEliminar.setCellFactory(new Callback<>() {
            @Override
            public TableCell<DetalleTemporal, Void> call(TableColumn<DetalleTemporal, Void> param) {
                return new TableCell<>() {
                    private final Button btnEliminar = new Button("Quitar");
                    private final HBox contenedor = new HBox(btnEliminar);

                    {
                        btnEliminar.getStyleClass().add("btn-acciones");
                        contenedor.setAlignment(Pos.CENTER);

                        btnEliminar.setOnAction(event -> {
                            DetalleTemporal selected = getTableView().getItems().get(getIndex());
                            listaDetalle.remove(selected);
                            actualizarTotal();
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : contenedor);
                    }
                };
            }
        });
    }

    private void actualizarTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleTemporal item : listaDetalle) {
            total = total.add(item.getSubtotal());
        }
        lblTotal.setText("S/ " + total.setScale(2, RoundingMode.HALF_UP).toString());
    }

    @FXML
    private void registrarCompra() {
        if (this.proveedorSeleccionado == null) {
            mostrarAlerta("Campos requeridos", "Debe seleccionar un proveedor.");
            return;
        }

        if (listaDetalle.isEmpty()) {
            mostrarAlerta("Tabla vacía", "Debe agregar al menos un producto a la compra.");
            return;
        }

        try {
            String supplierCode = this.proveedorSeleccionado.getCode();
            List<CreatePurchaseDetailRequest> details = new ArrayList<>();
            for (DetalleTemporal item : listaDetalle) {
                details.add(new CreatePurchaseDetailRequest(item.getProducto().getCode(), item.getPrecioCompra(), item.getQuantity()));
            }

            CreatePurchaseRequest request = new CreatePurchaseRequest(supplierCode, details);
            compraService.crearCompra(request);
            com.store.inventario.model.NavigationManager.getInstance().refreshAlerts();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Éxito");
            alert.setHeaderText("Compra Registrada");
            alert.setContentText("La compra se ha registrado y el stock de los productos se ha incrementado.");
            alert.showAndWait();

            cerrarModal();

        } catch (Exception e) {
            e.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("No se pudo registrar la compra");
            error.setContentText("Ocurrió un error al registrar la transacción: " + e.getMessage());
            error.showAndWait();
        }
    }

    @FXML
    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void actualizarMargen(BigDecimal purchasePrice, BigDecimal salePrice) {
        if (salePrice == null || salePrice.compareTo(BigDecimal.ZERO) == 0) {
            lblMargenRef.setText("Margen: N/A");
            return;
        }
        BigDecimal diff = salePrice.subtract(purchasePrice);
        BigDecimal margin = diff.divide(salePrice, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        lblMargenRef.setText("Margen: " + margin.setScale(1, RoundingMode.HALF_UP) + "%");
        if (margin.compareTo(BigDecimal.ZERO) < 0) {
            lblMargenRef.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else {
            lblMargenRef.setStyle("-fx-text-fill: #0D9488; -fx-font-size: 11px; -fx-font-weight: bold;");
        }
    }
}
