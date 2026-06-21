package com.store.inventario.controller.compra;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.compra.Compra;
import com.store.inventario.model.compra.CompraDetalle;
import com.store.inventario.service.compra.CompraService;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Collections;

public class CompraController {

    @FXML private TableView<Compra> tblCompras;
    @FXML private TableColumn<Compra, String> colCodigo;
    @FXML private TableColumn<Compra, String> colProveedor;
    @FXML private TableColumn<Compra, String> colUsuario;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, Integer> colCantidad;
    @FXML private TableColumn<Compra, BigDecimal> colTotal;
    @FXML private TableColumn<Compra, Void> colAcciones;

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

    private int paginaActual = 0;
    private final int tamanoPagina = 20;
    private int totalPaginas = 1;
    private final CompraService compraService = new CompraService();

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
            List<CompraDetalle> details = cellData.getValue().getDetails();
            int sum = 0;
            if (details != null) {
                sum = details.stream().mapToInt(CompraDetalle::getQuantity).sum();
            }
            return new SimpleIntegerProperty(sum).asObject();
        });

        colTotal.setCellValueFactory(cellData -> {
            List<CompraDetalle> details = cellData.getValue().getDetails();
            BigDecimal total = BigDecimal.ZERO;
            if (details != null) {
                for (CompraDetalle detail : details) {
                    BigDecimal price = detail.getPurchasePrice() != null ? detail.getPurchasePrice() : BigDecimal.ZERO;
                    total = total.add(price.multiply(BigDecimal.valueOf(detail.getQuantity())));
                }
            }
            return new SimpleObjectProperty<>(total);
        });
        configurarColumnaAcciones();
        btnAnterior.setOnAction(event -> handlePaginaAnterior());
        btnSiguiente.setOnAction(event -> handlePaginaSiguiente());
        obtenerCompras();
    }

    private void configurarColumnaAcciones() {
        Callback<TableColumn<Compra, Void>, TableCell<Compra, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Compra, Void> call(final TableColumn<Compra, Void> param) {
                return new TableCell<>() {
                    private final Button btnDetalle = new Button("Ver Detalle");
                    private final HBox contenedor = new HBox(btnDetalle);

                    {
                        btnDetalle.getStyleClass().add("btn-acciones");
                        contenedor.setAlignment(Pos.CENTER);

                        btnDetalle.setOnAction(event -> {
                            Compra compra = getTableView().getItems().get(getIndex());
                            handleVerDetalle(compra);
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

    private void handleVerDetalle(Compra compra) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/compras/compra-detail.fxml"));
                Parent root = loader.load();

                CompraDetailController controller = loader.getController();
                controller.setCompra(compra);

                Stage modal = new Stage();
                modal.initModality(Modality.APPLICATION_MODAL);
                modal.setTitle("Detalle de Compra - " + compra.getCode());
                modal.setScene(new Scene(root));
                modal.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No se pudo cargar el detalle");
                alert.setContentText("Ocurrió un error al cargar la vista de detalle: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void handlePaginaAnterior() {
        if (paginaActual > 0) {
            paginaActual--;
            obtenerCompras();
        }
    }

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
            PageResponse<Compra> response = compraService.obtenerCompra(search, paginaActual, tamanoPagina);
            List<Compra> compras = (response != null && response.getContent() != null) 
                    ? response.getContent() 
                    : Collections.emptyList();
            
            tblCompras.setItems(FXCollections.observableArrayList(compras));
            actualizarCartillasGenerales(compras);

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
            alert.showAndWait();
        }
    }

    @FXML
    private void ejecutarBusqueda() {
        obtenerCompras();
    }

    private void actualizarCartillasGenerales(List<Compra> compras) {
        if (compras == null || compras.isEmpty()) {
            if (lblComprasSemanales != null) lblComprasSemanales.setText("0");
            if (lblProductosIngresados != null) lblProductosIngresados.setText("0");
            if (lblProveedorFrecuente != null) lblProveedorFrecuente.setText("Ninguno");
            if (lblProveedorFrecuenteCompras != null) lblProveedorFrecuenteCompras.setText("0 compras");
            return;
        }

        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate sevenDaysAgo = today.minusDays(7);
        java.time.LocalDate thirtyDaysAgo = today.minusDays(30);

        int comprasSemanalesCount = 0;
        int productosMensualesCount = 0;
        java.util.Map<String, Integer> conteoProveedores = new java.util.HashMap<>();

        for (Compra c : compras) {
            if (c.getPurchaseDate() == null) continue;
            try {
                String dateStr = c.getPurchaseDate();
                java.time.LocalDateTime dateTime;
                if (dateStr.contains("T")) {
                    dateTime = java.time.LocalDateTime.parse(dateStr);
                } else {
                    dateTime = java.time.LocalDate.parse(dateStr).atStartOfDay();
                }
                java.time.LocalDate date = dateTime.toLocalDate();

                if (!date.isBefore(sevenDaysAgo)) {
                    comprasSemanalesCount++;
                }

                if (!date.isBefore(thirtyDaysAgo)) {
                    if (c.getDetails() != null) {
                        for (CompraDetalle d : c.getDetails()) {
                            productosMensualesCount += d.getQuantity();
                        }
                    }
                }
            } catch (Exception e) {

            }

            if (c.getSupplier() != null && c.getSupplier().getTradeName() != null) {
                String provName = c.getSupplier().getTradeName();
                conteoProveedores.put(provName, conteoProveedores.getOrDefault(provName, 0) + 1);
            }
        }

        String provFrecuente = "Ninguno";
        int maxComprasProv = 0;
        for (java.util.Map.Entry<String, Integer> entry : conteoProveedores.entrySet()) {
            if (entry.getValue() > maxComprasProv) {
                maxComprasProv = entry.getValue();
                provFrecuente = entry.getKey();
            }
        }

        if (lblComprasSemanales != null) {
            lblComprasSemanales.setText(String.valueOf(comprasSemanalesCount));
        }
        if (lblProductosIngresados != null) {
            lblProductosIngresados.setText(String.valueOf(productosMensualesCount));
        }
        if (lblProveedorFrecuente != null) {
            lblProveedorFrecuente.setText(provFrecuente);
        }
        if (lblProveedorFrecuenteCompras != null) {
            lblProveedorFrecuenteCompras.setText(maxComprasProv + (maxComprasProv == 1 ? " compra realizada" : " compras realizadas"));
        }
    }

    @FXML
    public void abrirModalNuevaCompra() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/compras/compra-form.fxml"));
        Parent root = loader.load();
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Nueva Compra");
        modal.setScene(new Scene(root));
        modal.showAndWait();

        obtenerCompras();
    }
}
