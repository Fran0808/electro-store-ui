package com.store.inventario.controller.guias;

import com.store.inventario.model.guia.GuideDetail;
import com.store.inventario.model.guia.InventoryGuide;
import com.store.inventario.service.guia.InventoryGuideService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class GuiaDetailController {
    @FXML
    private Label lblCodigo;
    @FXML
    private Label lblTipo;
    @FXML
    private Label lblBadgeTipo;
    @FXML
    private Label lblFechaHora;
    @FXML
    private Label lblMotivo;
    @FXML
    private Label lblUsuario;
    @FXML
    private Label lblDescripcion;
    @FXML
    private TableView<GuideDetail> tblProductos;
    @FXML
    private TableColumn<GuideDetail, String> colCodigo;
    @FXML
    private TableColumn<GuideDetail, String> colProducto;
    @FXML
    private TableColumn<GuideDetail, Integer> colCantidad;
    @FXML
    private Label lblTotalProductos;
    @FXML
    private Label lblTotalUnidades;
    @FXML
    private Button btnCerrar, btnExportar;

    private final InventoryGuideService guideService = new InventoryGuideService();

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(cellData -> {
            var product = cellData.getValue().getProduct();
            return new javafx.beans.property.SimpleStringProperty(product != null ? product.getCode() : "");
        });

        colProducto.setCellValueFactory(cellData -> {
            var product = cellData.getValue().getProduct();
            return new javafx.beans.property.SimpleStringProperty(product != null ? product.getName() : "");
        });

        colCantidad.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getQuantity()));
    }

    public void setGuideCode(String code) {
        try {
            InventoryGuide guide = guideService.obtenerGuiaPorCodigo(code);
            if (guide != null) {
                lblCodigo.setText(guide.getCode() != null ? guide.getCode() : "");
                
                String type = guide.getType();
                lblTipo.setText("ENTRY".equalsIgnoreCase(type) ? "Entrada" : "Salida");
                
                if ("ENTRY".equalsIgnoreCase(type)) {
                    lblBadgeTipo.setText("ENTRY");
                    lblBadgeTipo.getStyleClass().setAll("badge", "badge-success");
                } else {
                    lblBadgeTipo.setText("EXIT");
                    lblBadgeTipo.getStyleClass().setAll("badge", "badge-danger");
                }

                String dateStr = guide.getGuideDate();
                if (dateStr != null && dateStr.contains("T")) {
                    dateStr = dateStr.replace("T", " ");
                    if (dateStr.contains(".")) {
                        dateStr = dateStr.substring(0, dateStr.lastIndexOf("."));
                    }
                }
                lblFechaHora.setText(dateStr != null ? dateStr : "");

                lblMotivo.setText(guide.getReason() != null ? guide.getReason() : "");
                lblUsuario.setText(guide.getUser() != null ? guide.getUser().getUsername() : "");
                lblDescripcion.setText(guide.getDescription() != null ? guide.getDescription() : "");

                var details = guide.getDetails();
                if (details != null) {
                    tblProductos.setItems(FXCollections.observableArrayList(details));
                    lblTotalProductos.setText(String.valueOf(details.size()));
                    int totalUnits = details.stream().mapToInt(d -> d.getQuantity() != null ? d.getQuantity() : 0).sum();
                    lblTotalUnidades.setText(String.valueOf(totalUnits));
                } else {
                    tblProductos.setItems(FXCollections.emptyObservableList());
                    lblTotalProductos.setText("0");
                    lblTotalUnidades.setText("0");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error al cargar la guía");
            alert.setContentText("No se pudo obtener la información de la guía desde el servidor.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleExportar() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportar");
        alert.setHeaderText(null);
        alert.setContentText("Guía exportada correctamente");
        alert.showAndWait();
    }
}
