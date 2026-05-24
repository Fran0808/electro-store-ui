package com.store.inventario.controller.inventario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MovimientoFormController {
    @FXML private TextField txtCodigoGuia;
    @FXML private ComboBox<String> cbTipo;
    @FXML private ComboBox<String> cbUsuario;
    @FXML private DatePicker dpFecha;
    @FXML
    private TextField txtMotivo;
    @FXML private TextArea txtDescripcion;

    @FXML private ComboBox<String> cbProducto;
    @FXML private Spinner<Integer> spnCantidad;
    @FXML private TableView<Detalle> tblDetalle;
    @FXML private TableColumn<Detalle, String> colCod;
    @FXML private TableColumn<Detalle, String> colProd;
    @FXML private TableColumn<Detalle, Integer> colCant;
    @FXML private TableColumn<Detalle, Void> colAcc;

    @FXML private Button btnCancelar;

    private final ObservableList<Detalle> detalles = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbTipo.getItems().addAll("ENTRY", "EXIT");
        cbUsuario.getItems().addAll("admin","user1","user2");
        cbProducto.getItems().addAll("P001 - Televisor","P002 - Laptop","P003 - Smartphone");

        spnCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));

        colCod.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colProd.setCellValueFactory(new PropertyValueFactory<>("producto"));
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        tblDetalle.setItems(detalles);

        colAcc.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Quitar");
            {
                btn.getStyleClass().add("btn-secondary");
                btn.setOnAction(e -> {
                    Detalle d = getTableView().getItems().get(getIndex());
                    detalles.remove(d);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    @FXML
    private void handleAgregarProducto() {
        if (cbProducto.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione producto", "Elija un producto para agregar");
            return;
        }
        int cantidad = spnCantidad.getValue();
        if (cantidad <= 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Cantidad inválida", "La cantidad debe ser mayor que cero");
            return;
        }
        String prod = cbProducto.getValue();
        String cod = prod.contains(" - ") ? prod.split(" - ",2)[0] : prod;
        detalles.add(new Detalle(cod, prod, cantidad));
        cbProducto.getSelectionModel().clearSelection();
        spnCantidad.getValueFactory().setValue(1);
    }

    @FXML
    private void handleGuardarMovimiento() {
        if (txtCodigoGuia.getText().isEmpty() || cbTipo.getValue() == null || dpFecha.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos obligatorios", "Complete código, tipo y fecha.");
            return;
        }
        if (detalles.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Detalle vacío", "Agregue al menos un producto.");
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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static class Detalle {
        private final String codigo;
        private final String producto;
        private final int cantidad;
        public Detalle(String codigo, String producto, int cantidad) {
            this.codigo = codigo; this.producto = producto; this.cantidad = cantidad;
        }
        public String getCodigo() { return codigo; }
        public String getProducto() { return producto; }
        public int getCantidad() { return cantidad; }
    }
}
