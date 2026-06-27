package com.store.inventario.controller.ventas;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.clientes.Cliente;
import com.store.inventario.model.producto.Producto;
import com.store.inventario.model.ventas.CreateSaleDetailRequest;
import com.store.inventario.model.ventas.CreateSaleRequest;
import com.store.inventario.security.SessionManager;
import com.store.inventario.service.clientes.ClienteService;
import com.store.inventario.service.producto.ProductoService;
import com.store.inventario.service.venta.VentaService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaFormController {

    @FXML private TextField txtFecha;
    @FXML private ComboBox<String> cbCliente;
    @FXML private TextField txtVendedor;

    // Controles para el catálogo de productos
    @FXML private TableColumn<Producto, String> colCatCodigo;
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCatNombre;
    @FXML private TableColumn<Producto, Integer> colCatStock;
    @FXML private TableColumn<Producto, BigDecimal> colCatPrecio;
    @FXML private Label lblProductoSeleccionado;
    @FXML private Label lblPrecioListaRef;
    @FXML private Label lblDescuentoRef;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtPrecioVenta;
    @FXML private TextField txtBuscarProducto;

    // Controles para el detalle de la venta
    @FXML private TableView<VentaFormController.DetalleTemporal> tblDetalleVenta;
    @FXML private TableColumn<DetalleTemporal, String> colDetProducto;
    @FXML private TableColumn<DetalleTemporal, BigDecimal> colDetPrecio;
    @FXML private TableColumn<DetalleTemporal, Integer> colDetCantidad;
    @FXML private TableColumn<DetalleTemporal, BigDecimal> colDetSubtotal;
    @FXML private TableColumn<DetalleTemporal, Void> colEliminar;
    @FXML private Label lblTotalVenta;
    @FXML private Button btnCancelar;

    private ClienteService clienteService = new ClienteService();
    private ProductoService productoService = new ProductoService();
    private VentaService ventaService = new VentaService();

    private final ObservableList<Producto> listaCatalogProductos = FXCollections.observableArrayList();
    private final ObservableList<VentaFormController.DetalleTemporal> listaDetalle = FXCollections.observableArrayList();

    public static class DetalleTemporal {
        private final Producto producto;
        private final BigDecimal precioVenta;
        private final int cantidad;

        public DetalleTemporal(Producto producto, BigDecimal precioVenta, int cantidad) {
            this.producto = producto;
            this.precioVenta = precioVenta;
            this.cantidad = cantidad;
        }

        public Producto getProducto() {
            return producto;
        }
        public String getNombreProducto() {
            return producto != null ? producto.getName() : "";
        }
        public BigDecimal getPrecioVenta() {
            return precioVenta;
        }
        public int getQuantity() {
            return cantidad;
        }
        public BigDecimal getSubtotal() {
            return precioVenta != null ? precioVenta.multiply(BigDecimal.valueOf(cantidad)) : BigDecimal.ZERO;
        }
    }

    @FXML
    public void initialize() {
        String user = SessionManager.getInstance().getUsername();
        txtVendedor.setText(user != null && !user.isEmpty()
                ? user.substring(0, 1).toUpperCase() + user.substring(1)
                : "Sesión Activa");
        txtFecha.setText(LocalDate.now().toString());

        colCatCodigo.setCellValueFactory(new PropertyValueFactory<>("code"));
        colCatNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCatStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCatPrecio.setCellValueFactory(new PropertyValueFactory<>("salePrice"));

        colDetProducto.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNombreProducto()));
        colDetPrecio.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colDetCantidad.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colDetSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        configurarColumnaEliminar();

        tblDetalleVenta.setItems(listaDetalle);
        tblProductos.setItems(listaCatalogProductos);

        tblProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                lblProductoSeleccionado.setText("Producto: " + newSelection.getName() + " (" + newSelection.getCode() + ")");
                txtCantidad.setText("1");
                if (newSelection.getSalePrice() != null) {
                    BigDecimal listPrice = newSelection.getSalePrice();
                    lblPrecioListaRef.setText("Precio Lista Ref: S/ " + listPrice.setScale(2, RoundingMode.HALF_UP));
                    txtPrecioVenta.setText(listPrice.setScale(2, RoundingMode.HALF_UP).toString());
                    actualizarDescuento(listPrice, listPrice);
                } else {
                    lblPrecioListaRef.setText("Precio Lista Ref: S/ 0.00");
                    txtPrecioVenta.clear();
                    lblDescuentoRef.setText("Descuento: 0.0%");
                }
            } else {
                lblProductoSeleccionado.setText("Seleccione un producto del catálogo...");
                lblPrecioListaRef.setText("");
                lblDescuentoRef.setText("");
                txtCantidad.clear();
                txtPrecioVenta.clear();
            }
        });

        txtPrecioVenta.textProperty().addListener((obs, oldText, newText) -> {
            Producto selected = tblProductos.getSelectionModel().getSelectedItem();
            if (selected != null && selected.getSalePrice() != null && newText != null && !newText.trim().isEmpty()) {
                try {
                    BigDecimal currentPrice = new BigDecimal(newText.trim());
                    actualizarDescuento(selected.getSalePrice(), currentPrice);
                } catch (Exception e) {
                    lblDescuentoRef.setText("Descuento: Inválido");
                }
            }
        });

        cargarClientes();
        cargarProductos("");
    }

    private void cargarClientes() {
        Platform.runLater(() -> {
            try {
                PageResponse<Cliente> response = clienteService.listar(0, 100);
                if (response != null && response.getContent() != null) {
                    for (Cliente clie : response.getContent()) {
                        cbCliente.getItems().add(clie.getCode() + " - " + clie.getPerson().getFullName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void cargarProductos(String search) {
        Platform.runLater(() -> {
            try {
                PageResponse<Producto> response = productoService.obtenerProductos(search, 0, 100);
                if (response != null && response.getContent() != null) {
                    listaCatalogProductos.setAll(response.getContent());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

        String precioStr = txtPrecioVenta.getText();
        BigDecimal precio;
        try {
            precio = new BigDecimal(precioStr.trim());
            if (precio.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Precio unitario de venta inválido. Debe ser un número mayor a 0.");
            return;
        }

        listaDetalle.removeIf(d -> d.getProducto().getCode().equals(selected.getCode()));
        listaDetalle.add(new VentaFormController.DetalleTemporal(selected, precio, cantidad));
        actualizarTotal();
        tblProductos.getSelectionModel().clearSelection();
    }

    private void configurarColumnaEliminar() {
        colEliminar.setCellFactory(new Callback<>() {
            @Override
            public TableCell<VentaFormController.DetalleTemporal, Void> call(TableColumn<VentaFormController.DetalleTemporal, Void> param) {
                return new TableCell<>() {
                    private final Button btnEliminar = new Button("Quitar");
                    private final HBox contenedor = new HBox(btnEliminar);

                    {
                        btnEliminar.getStyleClass().add("btn-acciones");
                        contenedor.setAlignment(Pos.CENTER);

                        btnEliminar.setOnAction(event -> {
                            VentaFormController.DetalleTemporal selected = getTableView().getItems().get(getIndex());
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
        for (VentaFormController.DetalleTemporal item : listaDetalle) {
            total = total.add(item.getSubtotal());
        }
        lblTotalVenta.setText("S/ " + total.setScale(2, RoundingMode.HALF_UP).toString());
    }

    @FXML
    private void registrarVenta() {
        String clienteSeleccionado = cbCliente.getValue();
        if (clienteSeleccionado == null || clienteSeleccionado.isEmpty()) {
            mostrarAlerta("Campos requeridos", "Debe seleccionar un Cliente.");
            return;
        }

        if (listaDetalle.isEmpty()) {
            mostrarAlerta("Tabla vacía", "Debe agregar al menos un producto a la venta.");
            return;
        }

        try {
            String clientCode = clienteSeleccionado.split(" - ")[0];
            List<CreateSaleDetailRequest> details = new ArrayList<>();
            for (VentaFormController.DetalleTemporal item : listaDetalle) {
                details.add(new CreateSaleDetailRequest(item.getProducto().getCode(), item.getQuantity()));
            }

            CreateSaleRequest request = new CreateSaleRequest(clientCode, details);
            ventaService.crearVenta(request);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Éxito");
            alert.setHeaderText("Venta Registrada");
            alert.setContentText("La venta se ha registrado y el stock de los productos ha disminuído.");
            alert.showAndWait();
            cerrarModal();

        } catch (Exception e) {
            e.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("No se pudo registrar la venta");
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

    private void actualizarDescuento(BigDecimal listPrice, BigDecimal currentPrice) {
        if (listPrice == null || listPrice.compareTo(BigDecimal.ZERO) == 0) {
            lblDescuentoRef.setText("Descuento: 0.0%");
            return;
        }
        BigDecimal diff = listPrice.subtract(currentPrice);
        BigDecimal discount = diff.divide(listPrice, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            lblDescuentoRef.setText("Recargo: " + discount.negate().setScale(1, RoundingMode.HALF_UP) + "%");
            lblDescuentoRef.setStyle("-fx-text-fill: #3B82F6; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else {
            lblDescuentoRef.setText("Descuento: " + discount.setScale(1, RoundingMode.HALF_UP) + "%");
            lblDescuentoRef.setStyle("-fx-text-fill: #0D9488; -fx-font-size: 11px; -fx-font-weight: bold;");
        }
    }

}
