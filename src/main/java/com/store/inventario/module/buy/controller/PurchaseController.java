package com.store.inventario.module.buy.controller;

import com.store.inventario.shared.model.PageResponse;
import com.store.inventario.module.buy.model.entity.Purchase;
import com.store.inventario.module.buy.model.entity.PurchaseDetail;
import com.store.inventario.module.buy.service.PucharseService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import com.store.inventario.module.buy.model.entity.PurchaseMetrics;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Collections;

public class PurchaseController {

    @FXML private TableView<Purchase> tblCompras;
    @FXML private TableColumn<Purchase, String> colCodigo;
    @FXML private TableColumn<Purchase, String> colProveedor;
    @FXML private TableColumn<Purchase, String> colUsuario;
    @FXML private TableColumn<Purchase, String> colFecha;
    @FXML private TableColumn<Purchase, Integer> colCantidad;
    @FXML private TableColumn<Purchase, BigDecimal> colTotal;
    @FXML private TableColumn<Purchase, Void> colAcciones;

    @FXML private TextField txtBuscarCompra;
    @FXML private ComboBox<String> cbFecha;
    @FXML private ComboBox<String> cbProveedor;
    @FXML private ComboBox<String> cbUsuario;

    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Label lblResumenPaginacion;

    @FXML private Label lblComprasSemanales;
    @FXML private Label lblProductosIngresados;
    @FXML private Label lblProveedorFrecuente;
    @FXML private Label lblProveedorFrecuenteCompras;

    private List<Purchase> comprasOriginales = Collections.emptyList();
    private int paginaActual = 0;
    private final int tamanoPagina = 20;
    private int totalPaginas = 1;
    private final PucharseService pucharseService = new PucharseService();

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("code"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        colProveedor.setCellValueFactory(cellData -> {
            var supplier = cellData.getValue().getSupplier();
            return new SimpleStringProperty(supplier != null ? supplier.getTradeName() : "");
        });
        colUsuario.setCellValueFactory(cellData -> {
            var user = cellData.getValue().getUser();
            return new SimpleStringProperty(user != null ? user.getUsername() : "");
        });

        colCantidad.setCellValueFactory(cellData -> {
            List<PurchaseDetail> details = cellData.getValue().getDetails();
            int sum = 0;
            if (details != null) {
                sum = details.stream().mapToInt(PurchaseDetail::getQuantity).sum();
            }
            return new SimpleIntegerProperty(sum).asObject();
        });

        colTotal.setCellValueFactory(cellData -> {
            List<PurchaseDetail> details = cellData.getValue().getDetails();
            BigDecimal total = BigDecimal.ZERO;
            if (details != null) {
                for (PurchaseDetail detail : details) {
                    BigDecimal price = detail.getPurchasePrice() != null ? detail.getPurchasePrice() : BigDecimal.ZERO;
                    total = total.add(price.multiply(BigDecimal.valueOf(detail.getQuantity())));
                }
            }
            return new SimpleObjectProperty<>(total);
        });
        configurarColumnaAcciones();

        cbFecha.setItems(FXCollections.observableArrayList("Todas", "Hoy", "Esta semana", "Este mes"));
        cbFecha.getSelectionModel().selectFirst();

        cargarOpcionesFiltro();

        cbFecha.setOnAction(e -> { paginaActual = 0; obtenerCompras(); });
        cbProveedor.setOnAction(e -> { paginaActual = 0; obtenerCompras(); });
        cbUsuario.setOnAction(e -> { paginaActual = 0; obtenerCompras(); });
        if (txtBuscarCompra != null) {
            txtBuscarCompra.setOnAction(e -> { paginaActual = 0; obtenerCompras(); });
        }

        obtenerCompras();
        com.store.inventario.shared.utils.TableUtils.habilitarDobleClicParaCopiar(tblCompras);
    }

    private void configurarColumnaAcciones() {
        Callback<TableColumn<Purchase, Void>, TableCell<Purchase, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Purchase, Void> call(final TableColumn<Purchase, Void> param) {
                return new TableCell<>() {
                    private final Button btnDetalle = new Button("Ver Detalle");
                    private final HBox contenedor = new HBox(btnDetalle);

                    {
                        btnDetalle.getStyleClass().add("btn-acciones");
                        contenedor.setAlignment(Pos.CENTER);

                        btnDetalle.setOnAction(event -> {
                            Purchase purchase = getTableView().getItems().get(getIndex());
                            handleVerDetalle(purchase);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(contenedor);
                        }
                    }
                };
            }
        };
        colAcciones.setCellFactory(cellFactory);
    }

    private void handleVerDetalle(Purchase purchase) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/buy/purchase-detail.fxml"));
                Parent root = loader.load();

                PurchaseDetailController controller = loader.getController();
                controller.setCompra(purchase);

                Stage modal = new Stage();
                com.store.inventario.shared.utils.WindowUtils.applyIcon(modal);
                modal.initModality(Modality.APPLICATION_MODAL);
                modal.setTitle("Detalle de Compra - " + purchase.getCode());
                modal.setScene(new Scene(root));
                modal.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No se pudo cargar el detalle");
                alert.setContentText("Ocurrió un error al cargar la vista de detalle: " + e.getMessage());
                com.store.inventario.shared.utils.WindowUtils.applyIcon(alert);
                alert.showAndWait();
            }
        });
    }

    @FXML
    private void handlePaginaAnterior() {
        if (paginaActual > 0) {
            paginaActual--;
            obtenerCompras();
        }
    }

    @FXML
    private void handlePaginaSiguiente() {
        if (paginaActual < totalPaginas - 1) {
            paginaActual++;
            obtenerCompras();
        }
    }

    private void cargarOpcionesFiltro() {
        try {
            List<String> proveedores = pucharseService.obtenerProveedoresFiltro();
            cbProveedor.getItems().clear();
            cbProveedor.getItems().add("Todos");
            if (proveedores != null) {
                cbProveedor.getItems().addAll(proveedores);
            }
            cbProveedor.getSelectionModel().selectFirst();

            List<String> usuarios = pucharseService.obtenerUsuariosFiltro();
            cbUsuario.getItems().clear();
            cbUsuario.getItems().add("Todos");
            if (usuarios != null) {
                cbUsuario.getItems().addAll(usuarios);
            }
            cbUsuario.getSelectionModel().selectFirst();
        } catch (Exception e) {
            e.printStackTrace();
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
            case "Esta semana":
                start = now.with(java.time.DayOfWeek.MONDAY).with(java.time.LocalTime.MIN);
                end = now.with(java.time.LocalTime.MAX);
                break;
            case "Este mes":
                start = now.with(java.time.temporal.TemporalAdjusters.firstDayOfMonth()).with(java.time.LocalTime.MIN);
                end = now.with(java.time.LocalTime.MAX);
                break;
            default:
                return new String[]{null, null};
        }
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return new String[]{start.format(formatter), end.format(formatter)};
    }

    private void obtenerCompras() {
        try {
            String search = (txtBuscarCompra != null) ? txtBuscarCompra.getText().trim() : "";
            String supplier = (cbProveedor != null) ? cbProveedor.getValue() : "Todos";
            String user = (cbUsuario != null) ? cbUsuario.getValue() : "Todos";

            String[] fechas = calcularRangoFechas();
            String startDate = fechas[0];
            String endDate = fechas[1];

            PageResponse<Purchase> response = pucharseService.obtenerCompra(search, supplier, user, startDate, endDate, paginaActual, tamanoPagina);
            List<Purchase> purchases = (response != null && response.getContent() != null)
                    ? response.getContent() 
                    : Collections.emptyList();

            tblCompras.setItems(FXCollections.observableArrayList(purchases));
            cargarMetricas();

            totalPaginas = response != null ? response.getTotalPages() : 1;
            btnAnterior.setDisable(paginaActual == 0);
            btnSiguiente.setDisable(paginaActual >= totalPaginas - 1);

            if (lblResumenPaginacion != null && response != null) {
                long total = response.getTotalElements();
                int pageNum = response.getNumber();
                int pageSize = response.getSize();
                
                if (total == 0) {
                    lblResumenPaginacion.setText("No hay compras para mostrar");
                } else {
                    long desde = (long) pageNum * pageSize + 1;
                    long hasta = Math.min(desde + purchases.size() - 1, total);
                    lblResumenPaginacion.setText("Mostrando " + desde + "-" + hasta + " de " + total + " compras (Página " + (pageNum + 1) + " de " + totalPaginas + ")");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudieron obtener las compras");
            alert.setContentText("Ocurrió un error al cargar el listado de compras desde el servidor: " + e.getMessage());
            com.store.inventario.shared.utils.WindowUtils.applyIcon(alert);
            alert.showAndWait();
        }
    }

    @FXML
    private void limpiarFiltros() {
        if (txtBuscarCompra != null) txtBuscarCompra.clear();
        if (cbProveedor != null) cbProveedor.getSelectionModel().select("Todos");
        if (cbUsuario != null) cbUsuario.getSelectionModel().select("Todos");
        if (cbFecha != null) cbFecha.getSelectionModel().select("Todas");
        paginaActual = 0;
        obtenerCompras();
    }

    @FXML
    private void ejecutarBusqueda() {
        obtenerCompras();
    }

    private void cargarMetricas() {
        try {
            PurchaseMetrics metrics = pucharseService.obtenerMetricas();
            if (metrics != null) {
                if (lblComprasSemanales != null) {
                    lblComprasSemanales.setText(String.valueOf(metrics.getWeeklyPurchases()));
                }
                if (lblProductosIngresados != null) {
                    lblProductosIngresados.setText(String.valueOf(metrics.getMonthlyProductsEntered()));
                }
                if (lblProveedorFrecuente != null) {
                    lblProveedorFrecuente.setText(metrics.getFrequentSupplierName() != null ? metrics.getFrequentSupplierName() : "Ninguno");
                }
                if (lblProveedorFrecuenteCompras != null) {
                    long count = metrics.getFrequentSupplierCount();
                    lblProveedorFrecuenteCompras.setText(count + (count == 1 ? " compra realizada" : " compras realizadas"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirModalNuevaCompra() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/buy/purchase-form.fxml"));
        Parent root = loader.load();
        Stage modal = new Stage();
        com.store.inventario.shared.utils.WindowUtils.applyIcon(modal);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Nueva Compra");
        modal.setScene(new Scene(root));
        modal.showAndWait();

        obtenerCompras();
    }
}
