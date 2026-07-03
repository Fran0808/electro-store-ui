package com.store.inventario.controller.ventas;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.clientes.Cliente;
import com.store.inventario.model.clientes.CreateClienteRequest;
import com.store.inventario.model.persona.CreatePersonaRequest;
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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    @FXML private TextField txtCliente;
    @FXML private TextField txtVendedor;

    private Cliente clienteSeleccionado = null;

    // Controles para el catálogo de productos
    @FXML private TableColumn<Producto, String> colCatCodigo;
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCatNombre;
    @FXML private TableColumn<Producto, Integer> colCatStock;
    @FXML private TableColumn<Producto, BigDecimal> colCatPrecio;
    @FXML private Label lblProductoSeleccionado;
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
                    txtPrecioVenta.setText(listPrice.setScale(2, RoundingMode.HALF_UP).toString());
                } else {
                    txtPrecioVenta.clear();
                }
            } else {
                lblProductoSeleccionado.setText("Seleccione un producto del catálogo...");
                txtCantidad.clear();
                txtPrecioVenta.clear();
            }
        });

        seleccionarClientePorDefecto();
        cargarProductos("");
    }

    private void seleccionarClientePorDefecto() {
        Platform.runLater(() -> {
            try {
                PageResponse<Cliente> response = clienteService.obtenerClientes("Consumidor", 0, 10);
                Cliente consumidorFinal = null;

                if (response != null && response.getContent() != null) {
                    for (Cliente c : response.getContent()) {
                        if (c.getPerson() != null &&
                            "Consumidor".equalsIgnoreCase(c.getPerson().getFirstName()) &&
                            "Final".equalsIgnoreCase(c.getPerson().getLastName())) {
                            consumidorFinal = c;
                            break;
                        }
                    }
                }

                if (consumidorFinal != null) {
                    setClienteSeleccionado(consumidorFinal);
                } else {
                    CreatePersonaRequest personRequest = new CreatePersonaRequest("Consumidor", "Final", null, null);
                    CreateClienteRequest createRequest = new CreateClienteRequest(personRequest, null);
                    Cliente nuevoCliente = clienteService.crearCliente(createRequest);
                    if (nuevoCliente != null) {
                        setClienteSeleccionado(nuevoCliente);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al configurar el cliente por defecto: " + e.getMessage());
            }
        });
    }

    private void setClienteSeleccionado(Cliente cliente) {
        this.clienteSeleccionado = cliente;
        if (cliente != null && cliente.getPerson() != null) {
            txtCliente.setText(cliente.getCode() + " - " + cliente.getPerson().getFullName());
        } else {
            txtCliente.setText("Seleccione un cliente...");
        }
    }

    @FXML
    private void abrirBuscadorCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/clientes/cliente-search-modal.fxml"));
            Parent root = loader.load();

            com.store.inventario.controller.clientes.ClienteSearchModalController controller = loader.getController();

            Stage modal = new Stage();
            com.store.inventario.utils.WindowUtils.applyIcon(modal);
            modal.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            modal.setTitle("Buscar Cliente");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            Cliente seleccionado = controller.getClienteSeleccionado();
            if (seleccionado != null) {
                setClienteSeleccionado(seleccionado);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el buscador de clientes: " + e.getMessage());
        }
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

        int index = -1;
        for (int i = 0; i < listaDetalle.size(); i++) {
            if (listaDetalle.get(i).getProducto().getCode().equals(selected.getCode())) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            int nuevaCantidad = listaDetalle.get(index).getQuantity() + cantidad;
            listaDetalle.set(index, new VentaFormController.DetalleTemporal(selected, precio, nuevaCantidad));
        } else {
            listaDetalle.add(new VentaFormController.DetalleTemporal(selected, precio, cantidad));
        }
        
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
        if (this.clienteSeleccionado == null) {
            mostrarAlerta("Campos requeridos", "Debe seleccionar un Cliente.");
            return;
        }

        if (listaDetalle.isEmpty()) {
            mostrarAlerta("Tabla vacía", "Debe agregar al menos un producto a la venta.");
            return;
        }

        try {
            String clientCode = this.clienteSeleccionado.getCode();
            List<CreateSaleDetailRequest> details = new ArrayList<>();
            for (VentaFormController.DetalleTemporal item : listaDetalle) {
                details.add(new CreateSaleDetailRequest(item.getProducto().getCode(), item.getQuantity()));
            }

            CreateSaleRequest request = new CreateSaleRequest(clientCode, details);
            ventaService.crearVenta(request);
            com.store.inventario.model.NavigationManager.getInstance().refreshAlerts();

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



}
