package com.store.inventario.controller.inventario.guias;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GuiasController {
    @FXML
    private ComboBox<String> cbTipo;
    @FXML
    private ComboBox<String> cbUsuario;
    @FXML
    private TableView<GuiaRow> tblGuias;
    @FXML
    private TableColumn<GuiaRow, String> colCodigo, colFecha, colTipo, colMotivo, colUsuario;
    @FXML
    private TableColumn<GuiaRow, Integer> colProductos, colUnidades;
    @FXML
    private TableColumn<GuiaRow, Void> colAcciones;
    @FXML
    private Label lblResumenPaginacion;

    @FXML
    private void initialize(){
        cbTipo.getItems().addAll("ENTRY", "EXIT", "Todos");
        cbUsuario.getItems().addAll("Carlos Mendoza", "L. Vargas", "R. Quispe", "M. López", "Todos");

        colCodigo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCodigo()));
        colFecha.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFecha()));
        colTipo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipo()));
        colMotivo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMotivo()));
        colProductos.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getProductos()));
        colUnidades.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getUnidades()));
        colUsuario.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsuario()));

        colAcciones.setCellFactory(col -> new TableCell<GuiaRow, Void>() {
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

        ObservableList<GuiaRow> data = FXCollections.observableArrayList(
            new GuiaRow("GU-001", "20/11/2024", "ENTRY", "Conteo físico", 3, 125, "Carlos Mendoza"),
            new GuiaRow("GU-002", "19/11/2024", "EXIT", "Merma por vencimiento", 2, 45, "L. Vargas"),
            new GuiaRow("GU-003", "18/11/2024", "ENTRY", "Devolución interna", 5, 80, "R. Quispe")
        );
        tblGuias.setItems(data);
        lblResumenPaginacion.setText("Mostrando 1-3 de 3 guías");
    }

    @FXML
    private void handleNuevaGuia() throws IOException {
        FXMLLoader Loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/inventario/guias/guia-form.fxml"));
        Parent root = Loader.load();

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Agregar nueva guía");
        modal.setScene(new Scene(root));
        modal.showAndWait();
    }

    @FXML
    private void handleVerDetalle() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/inventario/guias/guia-detail.fxml"));
        Parent root = loader.load();

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Detalle de Guía");
        modal.setScene(new Scene(root));
        modal.showAndWait();
    }
    
    public static class GuiaRow {
        private final String codigo;
        private final String fecha;
        private final String tipo;
        private final String motivo;
        private final int productos;
        private final int unidades;
        private final String usuario;
        
        public GuiaRow(String codigo, String fecha, String tipo, String motivo, int productos, int unidades, String usuario) {
            this.codigo = codigo;
            this.fecha = fecha;
            this.tipo = tipo;
            this.motivo = motivo;
            this.productos = productos;
            this.unidades = unidades;
            this.usuario = usuario;
        }
        
        public String getCodigo() { return codigo; }
        public String getFecha() { return fecha; }
        public String getTipo() { return tipo; }
        public String getMotivo() { return motivo; }
        public int getProductos() { return productos; }
        public int getUnidades() { return unidades; }
        public String getUsuario() { return usuario; }
    }
}
