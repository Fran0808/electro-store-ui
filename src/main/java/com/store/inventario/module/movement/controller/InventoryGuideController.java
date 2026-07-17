package com.store.inventario.module.movement.controller;

import com.store.inventario.shared.model.PageResponse;
import com.store.inventario.module.movement.model.entity.InventoryGuide;
import com.store.inventario.module.movement.service.InventoryGuideService;
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

public class InventoryGuideController {
    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cbTipo;
    @FXML
    private ComboBox<String> cbFecha;
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
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnBuscar;

    private final InventoryGuideService guideService = new InventoryGuideService();
    private int paginaActual = 0;
    private final int tamanoPagina = 10;
    private int totalPaginas = 1;

    @FXML
    private void initialize() {
        if (cbTipo != null) {
            cbTipo.getItems().clear();
            cbTipo.getItems().addAll("Todos", "ENTRY", "EXIT");
            cbTipo.setValue("Todos");
        }

        if (cbFecha != null) {
            cbFecha.setValue("Todos");
        }

        if (btnAnterior != null) {
            btnAnterior.setOnAction(e -> handlePaginaAnterior());
        }
        if (btnSiguiente != null) {
            btnSiguiente.setOnAction(e -> handlePaginaSiguiente());
        }
        if (btnBuscar != null) {
            btnBuscar.setOnAction(e -> handleBuscar());
        }
        if (btnLimpiar != null) {
            btnLimpiar.setOnAction(e -> handleLimpiar());
        }

        if (txtBuscar != null) {
            txtBuscar.setOnAction(e -> handleBuscar());
        }

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
        com.store.inventario.shared.utils.TableUtils.habilitarDobleClicParaCopiar(tblGuias);
    }

    private void cargarGuias() {
        try {
            String search = (txtBuscar != null) ? txtBuscar.getText().trim() : "";
            String tipo = (cbTipo != null) ? cbTipo.getValue() : "Todos";

            String[] fechas = calcularRangoFechas();
            String startDate = fechas[0];
            String endDate = fechas[1];

            PageResponse<InventoryGuide> response = guideService.obtenerGuias(search, tipo, startDate, endDate, paginaActual, tamanoPagina);
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

    private String[] calcularRangoFechas() {
        if (cbFecha == null || cbFecha.getValue() == null) return new String[]{null, null};

        String opcion = cbFecha.getValue();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime start = null;
        java.time.LocalDateTime end = null;

        switch (opcion) {
            case "Hoy":
                start = now.with(java.time.LocalTime.MIN);
                end = now.with(java.time.LocalTime.MAX);
                break;
            case "Últimos 7 días":
                start = now.minusDays(7).with(java.time.LocalTime.MIN);
                end = now.with(java.time.LocalTime.MAX);
                break;
            case "Últimos 30 días":
                start = now.minusDays(30).with(java.time.LocalTime.MIN);
                end = now.with(java.time.LocalTime.MAX);
                break;
            case "Este mes":
                start = now.with(java.time.temporal.TemporalAdjusters.firstDayOfMonth()).with(java.time.LocalTime.MIN);
                end = now.with(java.time.LocalTime.MAX);
                break;
            default: // "Todos"
                return new String[]{null, null};
        }

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return new String[]{start.format(formatter), end.format(formatter)};
    }

    @FXML
    private void handleBuscar() {
        paginaActual = 0;
        cargarGuias();
    }

    @FXML
    private void handleLimpiar() {
        if (txtBuscar != null) txtBuscar.clear();
        if (cbTipo != null) cbTipo.setValue("Todos");
        if (cbFecha != null) cbFecha.setValue("Todos");
        paginaActual = 0;
        cargarGuias();
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/movement/inventory-guide-form.fxml"));
        Parent root = loader.load();

        Stage modal = new Stage();
        com.store.inventario.shared.utils.WindowUtils.applyIcon(modal);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Agregar nueva guía");
        modal.setScene(new Scene(root));
        modal.showAndWait();

        cargarGuias();
    }

    private void handleVerDetalle(InventoryGuide guide) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/movement/guide-detail.fxml"));
        Parent root = loader.load();

        InventoryGuideDetailController controller = loader.getController();
        controller.setGuideCode(guide.getCode());

        Stage modal = new Stage();
        com.store.inventario.shared.utils.WindowUtils.applyIcon(modal);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Detalle de Guía - " + guide.getCode());
        modal.setScene(new Scene(root));
        modal.showAndWait();
    }
}
