package com.store.inventario.module.buy.controller;

import com.store.inventario.model.PageResponse;
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

        obtenerCompras();
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/buy/purchase-detail.fxml"));
                Parent root = loader.load();

                PurchaseDetailController controller = loader.getController();
                controller.setCompra(purchase);

                Stage modal = new Stage();
                com.store.inventario.utils.WindowUtils.applyIcon(modal);
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
                com.store.inventario.utils.WindowUtils.applyIcon(alert);
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

    private void obtenerCompras() {
        String search = (txtBuscarCompra != null) ? txtBuscarCompra.getText().trim() : "";
        obtenerComprasConFiltro(search);
    }

    private void obtenerComprasConFiltro(String search) {
        try {
            PageResponse<Purchase> response = pucharseService.obtenerCompra(search, paginaActual, tamanoPagina);
            List<Purchase> purchases = (response != null && response.getContent() != null)
                    ? response.getContent() 
                    : Collections.emptyList();
            
            comprasOriginales = purchases;
            cargarProveedoresYUsuarios();
            
            tblCompras.setItems(FXCollections.observableArrayList(purchases));
            aplicarFiltrosLocales();
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
                    long hasta = Math.min(desde + pageSize - 1, total);
                    lblResumenPaginacion.setText("Mostrando " + desde + "-" + hasta + " de " + total + " compras (Página " + (pageNum + 1) + " de " + totalPaginas + ")");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudieron obtener las compras");
            alert.setContentText("Ocurrió un error al cargar el listado de compras desde el servidor: " + e.getMessage());
            com.store.inventario.utils.WindowUtils.applyIcon(alert);
            alert.showAndWait();
        }
    }

    private void cargarProveedoresYUsuarios() {
        String provSelected = cbProveedor.getValue();
        String userSelected = cbUsuario.getValue();

        cbProveedor.getItems().clear();
        cbProveedor.getItems().add("Todos");
        comprasOriginales.stream()
                .filter(c -> c.getSupplier() != null && c.getSupplier().getTradeName() != null)
                .map(c -> c.getSupplier().getTradeName())
                .distinct().sorted()
                .forEach(cbProveedor.getItems()::add);
        
        if (provSelected != null && cbProveedor.getItems().contains(provSelected)) {
            cbProveedor.setValue(provSelected);
        } else {
            cbProveedor.getSelectionModel().selectFirst();
        }

        cbUsuario.getItems().clear();
        cbUsuario.getItems().add("Todos");
        comprasOriginales.stream()
                .filter(c -> c.getUser() != null && c.getUser().getUsername() != null)
                .map(c -> c.getUser().getUsername())
                .distinct().sorted()
                .forEach(cbUsuario.getItems()::add);

        if (userSelected != null && cbUsuario.getItems().contains(userSelected)) {
            cbUsuario.setValue(userSelected);
        } else {
            cbUsuario.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void filtrarCompras() {
        aplicarFiltrosLocales();
    }

    private void aplicarFiltrosLocales() {
        String proveedor = cbProveedor.getValue();
        String usuario = cbUsuario.getValue();
        String rangoFecha = cbFecha.getValue();
        java.time.LocalDate hoy = java.time.LocalDate.now();

        List<Purchase> resultado = comprasOriginales.stream().filter(c -> {
            if (proveedor == null || proveedor.equals("Todos")) return true;
            return c.getSupplier() != null && proveedor.equals(c.getSupplier().getTradeName());
        }).filter(c -> {
            if (usuario == null || usuario.equals("Todos")) return true;
            return c.getUser() != null && usuario.equals(c.getUser().getUsername());
        }).filter(c -> {
            if (rangoFecha == null || rangoFecha.equals("Todas")) return true;
            if (c.getPurchaseDate() == null) return false;
            try {
                String dateStr = c.getPurchaseDate();
                java.time.LocalDate fechaCompra;
                if (dateStr.contains("T")) {
                    fechaCompra = java.time.LocalDateTime.parse(dateStr).toLocalDate();
                } else {
                    fechaCompra = java.time.LocalDate.parse(dateStr);
                }
                switch (rangoFecha) {
                    case "Hoy": return fechaCompra.equals(hoy);
                    case "Esta semana": return !fechaCompra.isBefore(hoy.with(java.time.DayOfWeek.MONDAY));
                    case "Este mes": return fechaCompra.getMonth() == hoy.getMonth() && fechaCompra.getYear() == hoy.getYear();
                    default: return true;
                }
            } catch (Exception e) {
                return false;
            }
        }).toList();

        tblCompras.setItems(FXCollections.observableArrayList(resultado));
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscarCompra.clear();
        cbProveedor.getSelectionModel().select("Todos");
        cbUsuario.getSelectionModel().select("Todos");
        cbFecha.getSelectionModel().select("Todas");
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/buy/purchase-form.fxml"));
        Parent root = loader.load();
        Stage modal = new Stage();
        com.store.inventario.utils.WindowUtils.applyIcon(modal);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Nueva Compra");
        modal.setScene(new Scene(root));
        modal.showAndWait();

        obtenerCompras();
    }
}
