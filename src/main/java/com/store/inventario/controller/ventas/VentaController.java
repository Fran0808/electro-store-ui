package com.store.inventario.controller.ventas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class VentaController {
    @FXML
    private ComboBox<String> cbTipoComprobante;
    @FXML
    private ComboBox<String> cbVendedor;
    @FXML
    private ComboBox<String> cbRangoFecha;
    @FXML
    private TableView<VentasRow>  tblHistorialVentas;
    @FXML
    private TableColumn<VentasRow, String> colCodigo, colFecha, colCliente, colVendedor;
    @FXML
    private TableColumn<VentasRow, Integer>  colArticulos;
    @FXML
    private TableColumn<VentasRow, Double>  colTotal;
    @FXML
    private TableColumn<VentaController.VentasRow, Void> colAcciones;

    @FXML
    public void initialize() {
        cbVendedor.getItems().addAll("Vendedor 1", "Vendedor 2", "Vendedor 3");
        cbRangoFecha.getItems().addAll("Hoy", "Últimos 7 días", "Últimos 30 días");

        colCodigo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCodigo()));
        colFecha.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFecha()));
        colCliente.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCliente()));
        colVendedor.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getVendedor()));
        colArticulos.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getArticulos()));
        colTotal.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getTotal()));

        colAcciones.setCellFactory( col -> new TableCell<VentasRow, Void>() {
            private final Button btnVer = new Button("Ver detalle");
            {
                btnVer.setStyle("-fx-background-color: #FFFFFF;\n" +
                        "    -fx-border-color: #E2E8F0;\n" +
                        "    -fx-border-width: 1px;\n" +
                        "    -fx-text-fill: #475569;\n" +
                        "    -fx-padding: 10px 15px; \n" +
                        "    -fx-cursor: hand;\n" +
                        "    -fx-font-size: 11px;\n" +
                        "    -fx-font-family: \"Inter\", \"Segoe UI\", sans-serif;\n" +
                        "    -fx-font-weight: bold;\n" +
                        "    -fx-background-radius: 4px;\n" +
                        "    -fx-border-radius: 4px;\n" +
                        "    -fx-min-height: 30px;\n" +
                        "    -fx-pref-height: 40px;\n" +
                        "    -fx-max-height: 40px;\n" +
                        "    -fx-transition: all 0.2s ease-in-out;"
                );
                btnVer.setOnAction(e -> {
                    try {
                        handleVerDetalle();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnVer);
            }
        });
        ObservableList<VentaController.VentasRow> data = FXCollections.observableArrayList(
                new VentaController.VentasRow("VEN-001", "24/05/2026 10:32", "Consumidor final", "Juan Pérez", 3, 250),
                new VentaController.VentasRow("VEN-002", "25/05/2026 11:12", "Consumidor final", "Pedro Suarez", 1, 150)
        );
        tblHistorialVentas.setItems(data);
    }

    @FXML
    private void handleForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/ventas/ventas-form.fxml"));
        Parent root = loader.load();

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Nueva venta");
        modal.setScene(new Scene(root));
        modal.showAndWait();
    }

    @FXML
    private void handleVerDetalle() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/ventas/venta-detail.fxml"));
        Parent root = loader.load();

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Detalle de Ventas");
        modal.setScene(new Scene(root));
        modal.showAndWait();
    }

    //Clase temporal para inserción de datos
    public static class VentasRow {
        private final String codigo;
        private final String fecha;
        private final String cliente;
        private final String vendedor;
        private final int articulos;
        private final double total;

        public VentasRow(String codigo, String fecha, String cliente, String vendedor, int articulos, double total) {
            this.codigo = codigo;
            this.fecha = fecha;
            this.cliente = cliente;
            this.vendedor = vendedor;
            this.articulos = articulos;
            this.total = total;
        }

        public String getCodigo() {
            return codigo;
        }

        public String getFecha() {
            return fecha;
        }

        public String getCliente() {
            return cliente;
        }

        public String getVendedor() {
            return vendedor;
        }

        public int getArticulos() {
            return articulos;
        }

        public double getTotal() {
            return total;
        }
    }
}
