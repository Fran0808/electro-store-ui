package com.store.inventario.controller.ventas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class VentaFormController {
    @FXML
    private TextField txtCodigoVenta;

    @FXML
    private ComboBox<String> cbCliente;

    @FXML
    private ComboBox<String> cbVendedor;

    @FXML
    private DatePicker dpFechaVenta;

    @FXML
    private ComboBox<String> cbProducto;

    @FXML
    private Spinner<Integer> spnCantidad;

    @FXML
    private TextField txtPrecioUnitario;

    @FXML
    private TableView<DetalleVenta> tblDetalleVenta;

    @FXML
    private TableColumn<DetalleVenta, String> colDetCodigo;

    @FXML
    private TableColumn<DetalleVenta, String> colDetProducto;

    @FXML
    private TableColumn<DetalleVenta, String> colDetPrecio;

    @FXML
    private TableColumn<DetalleVenta, Integer> colDetCantidad;

    @FXML
    private TableColumn<DetalleVenta, String> colDetSubtotal;

    @FXML
    private TableColumn<DetalleVenta, Void> colDetAcciones;

    @FXML
    private Label lblTotalVenta;

    @FXML
    private Button btnCancelar;

    private final ObservableList<DetalleVenta> detalleVentaList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbCliente.getItems().addAll(
                "Juan Pérez",
                "María García",
                "Carlos López",
                "Ana Torres"
        );

        cbVendedor.getItems().addAll(
                "Vendedor 1",
                "Vendedor 2",
                "Vendedor 3"
        );

        cbProducto.getItems().addAll(
                "P001 - Televisor LED 32\"",
                "P002 - Laptop Ultra",
                "P003 - Smartphone Pro",
                "P004 - Auriculares Bluetooth"
        );

        spnCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

        colDetCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colDetProducto.setCellValueFactory(new PropertyValueFactory<>("producto"));
        colDetPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colDetCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colDetSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        tblDetalleVenta.setItems(detalleVentaList);
        colDetAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEliminar.getStyleClass().add("btn-secondary");
                btnEliminar.setOnAction(event -> {
                    DetalleVenta detalle = getTableView().getItems().get(getIndex());
                    detalleVentaList.remove(detalle);
                    actualizarTotal();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEliminar);
                }
            }
        });

        actualizarTotal();
    }

    @FXML
    private void handleAgregarProducto() {
        if (cbProducto.getValue() == null || txtPrecioUnitario.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", "Seleccione un producto y escriba un precio");
            return;
        }

        String productoSeleccionado = cbProducto.getValue();
        String codigoProducto = extraerCodigo(productoSeleccionado);
        int cantidad = spnCantidad.getValue();
        double precio;

        try {
            precio = Double.parseDouble(txtPrecioUnitario.getText().trim());
            if (precio <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio inválido", "Ingrese un precio válido mayor que cero");
            return;
        }

        DetalleVenta detalle = new DetalleVenta(codigoProducto, productoSeleccionado, String.format("S/ %.2f", precio), cantidad, String.format("S/ %.2f", precio * cantidad));
        detalleVentaList.add(detalle);
        tblDetalleVenta.refresh();
        actualizarTotal();

        cbProducto.getSelectionModel().clearSelection();
        spnCantidad.getValueFactory().setValue(1);
        txtPrecioUnitario.clear();
    }

    @FXML
    private void handleRegistrarVenta() {
        if (txtCodigoVenta.getText().isEmpty() || cbVendedor.getValue() == null || dpFechaVenta.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos obligatorios", "Complete el código de venta, el vendedor y la fecha de venta.");
            return;
        }

        if (detalleVentaList.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Detalle vacío", "Agregue al menos un producto para registrar la venta.");
            return;
        }

        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void actualizarTotal() {
        double total = detalleVentaList.stream()
                .mapToDouble(detalle -> {
                    String subtotalText = detalle.getSubtotal().replace("S/", "").trim();
                    return Double.parseDouble(subtotalText);
                })
                .sum();
        lblTotalVenta.setText(String.format("S/ %.2f", total));
    }

    private String extraerCodigo(String producto) {
        if (producto != null && producto.contains(" - ")) {
            return producto.split(" - ", 2)[0];
        }
        return "";
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static class DetalleVenta {
        private final String codigo;
        private final String producto;
        private final String precio;
        private final int cantidad;
        private final String subtotal;

        public DetalleVenta(String codigo, String producto, String precio, int cantidad, String subtotal) {
            this.codigo = codigo;
            this.producto = producto;
            this.precio = precio;
            this.cantidad = cantidad;
            this.subtotal = subtotal;
        }

        public String getCodigo() {
            return codigo;
        }

        public String getProducto() {
            return producto;
        }

        public String getPrecio() {
            return precio;
        }

        public int getCantidad() {
            return cantidad;
        }

        public String getSubtotal() {
            return subtotal;
        }
    }
}
