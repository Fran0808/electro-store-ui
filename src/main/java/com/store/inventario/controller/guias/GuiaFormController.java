package com.store.inventario.controller.guias;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.guia.CreateGuideDetailRequest;
import com.store.inventario.model.guia.CreateInventoryGuideRequest;
import com.store.inventario.model.guia.DetalleFila;
import com.store.inventario.model.guia.InventoryGuide;
import com.store.inventario.module.product.model.entity.Product;
import com.store.inventario.security.SessionManager;
import com.store.inventario.service.guia.InventoryGuideService;
import com.store.inventario.module.product.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GuiaFormController {

    @FXML private TextField txtMotivo;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtDescripcion;
    @FXML private RadioButton rbEntry;
    @FXML private RadioButton rbExit;

    @FXML private TextField txtBuscarProducto;
    @FXML private Button btnBuscarProducto;
    @FXML private TextField txtStockActual;
    @FXML private TextField txtCantidad;

    @FXML private TableView<Product> tblCatalogo;
    @FXML private TableColumn<Product, String> colCatCod;
    @FXML private TableColumn<Product, String> colCatProducto;
    @FXML private TableColumn<Product, Integer> colCatStock;
    @FXML private Label lblProductoSeleccionado;

    @FXML private TableView<DetalleFila> tblDetalle;
    @FXML private TableColumn<DetalleFila, String> colCod;
    @FXML private TableColumn<DetalleFila, String> colProducto;
    @FXML private TableColumn<DetalleFila, Integer> colStockActual;
    @FXML private TableColumn<DetalleFila, Integer> colCantidad;
    @FXML private TableColumn<DetalleFila, Void> colAcciones;

    @FXML private Label lblTotalProductos;
    @FXML private Label lblTotalUnidades;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    private final InventoryGuideService guideService = new InventoryGuideService();
    private final ProductService productService = new ProductService();
    private final ObservableList<DetalleFila> filas = FXCollections.observableArrayList();
    private List<Product> listaProducts = new ArrayList<>();
    private Product productSeleccionado;

    @FXML
    public void initialize() {
        String user = SessionManager.getInstance().getUsername();
        txtUsuario.setText(user != null && !user.isEmpty()
                ? user.substring(0, 1).toUpperCase() + user.substring(1)
                : "Sesión Activa");

        rbEntry.setSelected(true);

        colCatCod.setCellValueFactory(new PropertyValueFactory<>("code"));
        colCatProducto.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCatStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        tblCatalogo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                productSeleccionado = newVal;
                if (lblProductoSeleccionado != null) {
                    lblProductoSeleccionado.setText("Producto: " + newVal.getName() + " (" + newVal.getCode() + ")");
                }
                txtStockActual.setText(String.valueOf(newVal.getStock()));
            } else {
                if (lblProductoSeleccionado != null) {
                    lblProductoSeleccionado.setText("Seleccione un producto del catálogo...");
                }
                txtStockActual.setText("");
            }
        });

        colCod.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        configurarColumnaQuitar();
        tblDetalle.setItems(filas);

        cargarProductos();
    }

    private void cargarProductos() {
        try {
            PageResponse<Product> response = productService.obtenerProductos();
            if (response != null && response.getContent() != null) {
                listaProducts = response.getContent();
                tblCatalogo.setItems(FXCollections.observableArrayList(listaProducts));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configurarColumnaQuitar() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnQuitar = new Button("Quitar");
            {
                btnQuitar.getStyleClass().add("btn-acciones");
                btnQuitar.setOnAction(e -> {
                    DetalleFila selected = getTableRow().getItem();
                    if (selected != null) {
                        filas.remove(selected);
                        actualizarResumen();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnQuitar);
            }
        });
    }

    @FXML
    private void handleBuscarProducto() {
        String busqueda = txtBuscarProducto.getText() != null ? txtBuscarProducto.getText().trim().toLowerCase() : "";
        List<Product> filtrados = listaProducts.stream()
                .filter(p -> (p.getName() != null && p.getName().toLowerCase().contains(busqueda)) ||
                             (p.getCode() != null && p.getCode().toLowerCase().contains(busqueda)))
                .toList();
        tblCatalogo.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    private void handleAgregarProducto() {
        if (productSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Primero seleccione un producto del catálogo.");
            return;
        }

        int cantidad = parseCantidad();
        if (cantidad <= 0) return;

        String code = productSeleccionado.getCode();

        DetalleFila existente = filas.stream()
                .filter(f -> f.getCodigo().equals(code))
                .findFirst()
                .orElse(null);

        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + cantidad);
            tblDetalle.refresh();
        } else {
            filas.add(new DetalleFila(
                    code,
                    productSeleccionado.getName(),
                    productSeleccionado.getStock(),
                    cantidad
            ));
        }

        tblCatalogo.getSelectionModel().clearSelection();
        productSeleccionado = null;
        txtStockActual.clear();
        txtCantidad.clear();
        actualizarResumen();
    }

    @FXML
    private void handleGuardar() {
        String motivo = txtMotivo.getText() != null ? txtMotivo.getText().trim() : "";
        if (motivo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe ingresar el motivo de la guía.");
            return;
        }
        if (filas.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debe agregar al menos un producto a la guía.");
            return;
        }

        String tipo = rbEntry.isSelected() ? "ENTRY" : "EXIT";
        String desc = txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "";

        List<CreateGuideDetailRequest> details = new ArrayList<>();
        for (DetalleFila f : filas) {
            details.add(new CreateGuideDetailRequest(f.getCodigo(), f.getCantidad()));
        }

        try {
            InventoryGuide guide = guideService.crearGuia(
                    new CreateInventoryGuideRequest(tipo, motivo, desc, details)
            );
            com.store.inventario.model.NavigationManager.getInstance().refreshAlerts();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Guía " + guide.getCode() + " registrada correctamente.");
            cerrarVentana(btnGuardar);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Guardar",
                    "No se pudo crear la guía: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana(btnCancelar);
    }

    private int parseCantidad() {
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "La cantidad debe ser mayor a 0.");
                return -1;
            }
            return cantidad;
        } catch (NumberFormatException | NullPointerException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Ingrese una cantidad numérica válida.");
            return -1;
        }
    }

    private void actualizarResumen() {
        lblTotalProductos.setText(String.valueOf(filas.size()));
        lblTotalUnidades.setText(String.valueOf(
                filas.stream().mapToInt(DetalleFila::getCantidad).sum()
        ));
    }

    private void cerrarVentana(Button origen) {
        ((Stage) origen.getScene().getWindow()).close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        com.store.inventario.utils.WindowUtils.applyIcon(alerta);
        alerta.showAndWait();
    }
}
