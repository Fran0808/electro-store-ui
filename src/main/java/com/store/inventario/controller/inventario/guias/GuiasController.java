package com.store.inventario.controller.inventario.guias;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.guia.InventoryGuide;
import com.store.inventario.service.guia.InventoryGuideService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class GuiasController {
    @FXML
    private ComboBox<String> cbTipo;
    @FXML
    private ComboBox<String> cbUsuario;
    @FXML
    private TableView<InventoryGuide> tblGuias;
    @FXML
    private TableColumn<InventoryGuide, String> colCodigo;
    @FXML
    private TableColumn<InventoryGuide, String> colFecha;
    @FXML
    private TableColumn<InventoryGuide, String> colTipo;
    @FXML
    private TableColumn<InventoryGuide, String> colMotivo;
    @FXML
    private TableColumn<InventoryGuide, String> colUsuario;
    @FXML
    private TableColumn<InventoryGuide, Integer> colProductos;
    @FXML
    private TableColumn<InventoryGuide, Integer> colUnidades;
    @FXML
    private TableColumn<InventoryGuide, Void> colAcciones;
    @FXML
    private Label lblResumenPaginacion;

    private final InventoryGuideService guideService = new InventoryGuideService();

    @FXML
    private void initialize() {
        cbTipo.getItems().addAll("ENTRY", "EXIT", "Todos");
        cbUsuario.getItems().addAll("Todos");

        colCodigo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCode()));
        
        colFecha.setCellValueFactory(cellData -> {
            String dateStr = cellData.getValue().getGuideDate();
            if (dateStr != null && dateStr.contains("T")) {
                dateStr = dateStr.replace("T", " ");
                if (dateStr.contains(".")) {
                    dateStr = dateStr.substring(0, dateStr.lastIndexOf("."));
                }
            }
            return new javafx.beans.property.SimpleStringProperty(dateStr != null ? dateStr : "");
        });

        colTipo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));
        colMotivo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getReason()));
        
        colProductos.setCellValueFactory(cellData -> {
            var details = cellData.getValue().getDetails();
            int count = (details != null) ? details.size() : 0;
            return new javafx.beans.property.SimpleObjectProperty<>(count);
        });

        colUnidades.setCellValueFactory(cellData -> {
            var details = cellData.getValue().getDetails();
            int sum = 0;
            if (details != null) {
                for (var d : details) {
                    sum += (d.getQuantity() != null) ? d.getQuantity() : 0;
                }
            }
            return new javafx.beans.property.SimpleObjectProperty<>(sum);
        });

        colUsuario.setCellValueFactory(cellData -> {
            var user = cellData.getValue().getUser();
            return new javafx.beans.property.SimpleStringProperty(user != null ? user.getUsername() : "");
        });

        colAcciones.setCellFactory(col -> new TableCell<InventoryGuide, Void>() {
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

        cargarGuias();
    }

    private void cargarGuias() {
        try {
            PageResponse<InventoryGuide> response = guideService.obtenerGuias();
            List<InventoryGuide> guias = (response != null) ? response.getContent() : java.util.Collections.emptyList();
            tblGuias.setItems(FXCollections.observableArrayList(guias));
            
            if (response != null) {
                long total = response.getTotalElements();
                lblResumenPaginacion.setText("Mostrando 1-" + guias.size() + " de " + total + " guías");
            } else {
                lblResumenPaginacion.setText("Mostrando 0-0 de 0 guías");
            }
        } catch (Exception e) {
            e.printStackTrace();
            lblResumenPaginacion.setText("Error al cargar guías desde el servidor");
        }
    }

    @FXML
    private void handleNuevaGuia() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/inventario/guias/guia-form.fxml"));
        Parent root = loader.load();

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Agregar nueva guía");
        modal.setScene(new Scene(root));
        modal.showAndWait();

        cargarGuias();
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
}
