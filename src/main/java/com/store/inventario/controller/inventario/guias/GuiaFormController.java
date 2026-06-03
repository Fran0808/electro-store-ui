package com.store.inventario.controller.inventario.guias;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.guia.CreateGuideDetailRequest;
import com.store.inventario.model.guia.CreateInventoryGuideRequest;
import com.store.inventario.model.guia.InventoryGuide;
import com.store.inventario.model.guia.DetalleFila;
import com.store.inventario.model.producto.Producto;
import com.store.inventario.service.guia.InventoryGuideService;
import com.store.inventario.service.producto.ProductoService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GuiaFormController {
    @FXML
    private TextField txtMotivo;
    @FXML
    private TextField txtUsuario;
    @FXML
    private TextField txtStockActual;
    @FXML
    private RadioButton rbEntry;
    @FXML
    private RadioButton rbExit;
    @FXML
    private TextField txtDescripcion;
    @FXML
    private ComboBox<String> cbProducto;
    @FXML
    private Spinner<Integer> spnCantidad;
    @FXML
    private TableView<DetalleFila> tblDetalle;
    @FXML
    private TableColumn<DetalleFila, String> colCod;
    @FXML
    private TableColumn<DetalleFila, String> colProducto;
    @FXML
    private TableColumn<DetalleFila, Integer> colCantidad;
    @FXML
    private TableColumn<DetalleFila, Void> colAcciones;
    @FXML
    private Label lblTotalProductos;
    @FXML
    private Label lblTotalUnidades;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnGuardar;

    private final InventoryGuideService guideService = new InventoryGuideService();
    private final ProductoService productoService = new ProductoService();
    private final ObservableList<DetalleFila> filas = FXCollections.observableArrayList();
    private List<Producto> listaProductos = new ArrayList<>();

    @FXML
    public void initialize() {
        String activeUser = com.store.inventario.security.SessionManager.getInstance().getUsername();
        if (activeUser != null && !activeUser.isEmpty()) {
            txtUsuario.setText(activeUser.substring(0, 1).toUpperCase() + activeUser.substring(1));
        } else {
            txtUsuario.setText("Sesión Activa");
        }

        rbEntry.setSelected(true);
        spnCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 1));

        colCod.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        tblDetalle.setItems(filas);

        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnQuitar = new Button("Quitar");
            {
                btnQuitar.getStyleClass().add("btn-secondary");
                btnQuitar.setOnAction(e -> {
                    DetalleFila fila = getTableView().getItems().get(getIndex());
                    filas.remove(fila);
                    actualizarResumen();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnQuitar);
            }
        });

        cargarProductos();

        cbProducto.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String productCode = newValue.split(" - ")[0];
                Producto p = listaProductos.stream()
                        .filter(prod -> prod.getCode().equals(productCode))
                        .findFirst()
                        .orElse(null);
                if (p != null) {
                    txtStockActual.setText(String.valueOf(p.getStock()));
                }
            } else {
                txtStockActual.setText("");
            }
        });
    }

    private void cargarProductos() {
        try {
            PageResponse<Producto> response = productoService.obtenerProductos();
            if (response != null && response.getContent() != null) {
                listaProductos = response.getContent();
                ObservableList<String> items = FXCollections.observableArrayList();
                for (Producto p : listaProductos) {
                    items.add(p.getCode() + " - " + p.getName());
                }
                cbProducto.setItems(items);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAgregarProducto() {
        String prodSeleccionado = cbProducto.getValue();
        if (prodSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Seleccione un producto para agregar.");
            return;
        }

        int cantidad = spnCantidad.getValue();
        if (cantidad <= 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Ingrese una cantidad válida.");
            return;
        }

        String[] parts = prodSeleccionado.split(" - ", 2);
        String productCode = parts[0];
        String productName = parts[1];

        // Verificar si ya está en la lista
        DetalleFila existente = filas.stream()
                .filter(f -> f.getCodigo().equals(productCode))
                .findFirst()
                .orElse(null);

        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + cantidad);
            tblDetalle.refresh();
        } else {
            filas.add(new DetalleFila(productCode, productName, cantidad));
        }

        cbProducto.setValue(null);
        spnCantidad.getValueFactory().setValue(1);
        actualizarResumen();
    }

    private void actualizarResumen() {
        lblTotalProductos.setText(String.valueOf(filas.size()));
        int totalUnidades = filas.stream().mapToInt(DetalleFila::getCantidad).sum();
        lblTotalUnidades.setText(String.valueOf(totalUnidades));
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
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

        CreateInventoryGuideRequest request = new CreateInventoryGuideRequest(tipo, motivo, desc, details);

        Platform.runLater(() -> {
            try {
                InventoryGuide guide = guideService.crearGuia(request);
                
                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Éxito");
                exito.setHeaderText("Guía Creada");
                exito.setContentText("La guía de inventario " + guide.getCode() + " se ha registrado correctamente.");
                exito.showAndWait();

                Stage stage = (Stage) btnGuardar.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                e.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error al Guardar");
                error.setHeaderText("No se pudo crear la guía");
                error.setContentText("Ocurrió un error en el servidor: " + e.getMessage());
                error.showAndWait();
            }
        });
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
