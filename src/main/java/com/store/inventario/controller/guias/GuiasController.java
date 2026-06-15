package com.store.inventario.controller.guias;

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

    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnSiguiente;

    private final InventoryGuideService guideService = new InventoryGuideService();
    private int paginaActual = 0;
    private final int tamanoPagina = 10;
    private int totalPaginas = 1;

    @FXML
    private void initialize() {
        cbTipo.getItems().addAll("ENTRY", "EXIT", "Todos");
        cbUsuario.getItems().addAll("Todos");

        btnAnterior.setOnAction(e -> handlePaginaAnterior());
        btnSiguiente.setOnAction(e -> handlePaginaSiguiente());

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
                btnVer.getStyleClass().add("btn-acciones");
                btnVer.setOnAction(e -> {
                    InventoryGuide selected = getTableRow().getItem();
                    if (selected != null) {
                        try {
                            handleVerDetalle(selected);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
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
            PageResponse<InventoryGuide> response = guideService.obtenerGuias(paginaActual, tamanoPagina);
            List<InventoryGuide> guias = (response != null) ? response.getContent() : java.util.Collections.emptyList();
            tblGuias.setItems(FXCollections.observableArrayList(guias));
            
            if (response != null) {
                totalPaginas = response.getTotalPages();
                btnAnterior.setDisable(paginaActual == 0);
                btnSiguiente.setDisable(paginaActual >= totalPaginas - 1);

                long total = response.getTotalElements();
                int pageSize = response.getSize();
                long desde = (long) paginaActual * pageSize + 1;
                long hasta = Math.min(desde + guias.size() - 1, total);
                if (total == 0) {
                    lblResumenPaginacion.setText("No hay guías para mostrar");
                } else {
                    lblResumenPaginacion.setText("Mostrando " + desde + "-" + hasta + " de " + total + " guías (Página " + (paginaActual + 1) + " de " + totalPaginas + ")");
                }
            } else {
                totalPaginas = 1;
                btnAnterior.setDisable(true);
                btnSiguiente.setDisable(true);
                lblResumenPaginacion.setText("Mostrando 0-0 de 0 guías");
            }
        } catch (Exception e) {
            e.printStackTrace();
            lblResumenPaginacion.setText("Error al cargar guías desde el servidor");
        }
    }

    private void handlePaginaAnterior() {
        if (paginaActual > 0) {
            paginaActual--;
            cargarGuias();
        }
    }

    private void handlePaginaSiguiente() {
        if (paginaActual < totalPaginas - 1) {
            paginaActual++;
            cargarGuias();
        }
    }

    @FXML
    private void handleNuevaGuia() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/guias/guia-form.fxml"));
        Parent root = loader.load();

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Agregar nueva guía");
        modal.setScene(new Scene(root));
        modal.showAndWait();

        cargarGuias();
    }

    private void handleVerDetalle(InventoryGuide guide) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/guias/guia-detail.fxml"));
        Parent root = loader.load();

        GuiaDetailController controller = loader.getController();
        controller.setGuideCode(guide.getCode());

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Detalle de Guía - " + guide.getCode());
        modal.setScene(new Scene(root));
        modal.showAndWait();
    }
}
