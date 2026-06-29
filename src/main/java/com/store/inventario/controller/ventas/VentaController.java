package com.store.inventario.controller.ventas;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.ventas.Venta;
import com.store.inventario.model.ventas.VentaDetalle;
import com.store.inventario.model.ventas.VentaMetrics;
import com.store.inventario.service.venta.VentaService;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.util.Callback;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class VentaController {
    @FXML private Button btnNuevaVenta;
    @FXML private VBox containerContenido;
    @FXML private Label lblVendidoHoy;
    @FXML private Label lblTotalTransacciones;
    @FXML private Label lblTicketPromedio;
    @FXML private TextField txtBuscarVenta;
    @FXML private Button btnLimpiarVenta;
    @FXML private Button btnBuscarVenta;
    @FXML private Label lblResumenPaginacion;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private ComboBox<String> cbTipoComprobante;
    @FXML private ComboBox<String> cbVendedor;
    @FXML private ComboBox<String> cbRangoFecha;
    @FXML private TableView<Venta>  tblVentas;
    @FXML private TableColumn<Venta, String> colCodigo, colFecha, colCliente, colVendedor;
    @FXML private TableColumn<Venta, Integer>  colArticulos;
    @FXML private TableColumn<Venta, BigDecimal>  colTotal;
    @FXML private TableColumn<Venta, Void> colAcciones;

    private final VentaService ventaService = new VentaService();
    private List<Venta> ventasOriginales = Collections.emptyList();
    private List<Venta> ventasFiltradas = Collections.emptyList();
    private int paginaActual = 0;
    private final int tamanoPagina = 20;
    private int totalPaginas = 1;

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory((new PropertyValueFactory<>("code")));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        colCliente.setCellValueFactory(cellData -> {
               var customer = cellData.getValue().getCustomer();
               return new SimpleStringProperty(customer != null && customer.getPerson() != null ? customer.getPerson().getFullName() : "");
        });
        colVendedor.setCellValueFactory(cellData -> {
                var user = cellData.getValue().getUser();
                return new SimpleStringProperty(user != null ? user.getUsername() : "");
        });
        colArticulos.setCellValueFactory(cellData -> {
            List<VentaDetalle> details = cellData.getValue().getDetails();
            int cantidad = 0;
            if (details != null) {
                cantidad = details.stream().mapToInt(VentaDetalle::getQuantity).sum();
            }
            return new SimpleIntegerProperty(cantidad).asObject();
        });
        colTotal.setCellValueFactory(cellData -> {
            List<VentaDetalle> details = cellData.getValue().getDetails();
            BigDecimal total = BigDecimal.ZERO;
            if (details != null) {
                for (VentaDetalle detail : details) {
                    BigDecimal price = detail.getSalePrice() != null ? detail.getSalePrice() : BigDecimal.ZERO;
                    total = total.add(price.multiply(BigDecimal.valueOf(detail.getQuantity())));
                }
            }
            return new SimpleObjectProperty<>(total);
        });
        configurarColumnaAcciones();
        cbRangoFecha.setItems(FXCollections.observableArrayList("Todas","Hoy", "Esta semana", "Este mes"));
        cbRangoFecha.getSelectionModel().selectFirst();
        obtenerVentas();
    }

    private void configurarColumnaAcciones() {
        Callback<TableColumn<Venta, Void>, TableCell<Venta, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Venta, Void> call(final TableColumn<Venta, Void> param) {
                return new TableCell<>(){
                    private final Button btnDetalle = new Button("Ver Detalle");
                    private final HBox contenedor = new HBox(btnDetalle);
                    {
                        btnDetalle.getStyleClass().add("btn-acciones");
                        contenedor.setAlignment(Pos.CENTER);
                        btnDetalle.setOnAction(event -> {
                            Venta venta = getTableView().getItems().get(getIndex());
                            handleVerDetalle(venta);
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

    private void handleVerDetalle(Venta venta){
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/ventas/venta-detail.fxml"));
                Parent root = loader.load();
                VentaDetailController controller = loader.getController();
                controller.setVenta(venta);
                Stage modal = new Stage();
                com.store.inventario.utils.WindowUtils.applyIcon(modal);
                modal.initModality(Modality.APPLICATION_MODAL);
                modal.setTitle("Detalle de Venta - " + venta.getCode());
                modal.setScene(new Scene(root));
                modal.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No se pudo cargar el detalle");
                alert.setContentText("Ocurrio un error al cargar la vista de detalle: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    @FXML
    private void handlePaginaAnterior() {
        if (paginaActual > 0) {
            paginaActual--;
            obtenerVentas();
        }
    }

    @FXML
    private void handlePaginaSiguiente() {
        if (paginaActual < totalPaginas - 1) {
            paginaActual++;
            obtenerVentas();
        }
    }

    private void obtenerVentas(){
        String search = (txtBuscarVenta != null) ? txtBuscarVenta.getText().trim() : "";
        obtenerVentasConFiltro(search);
    }

    private void obtenerVentasConFiltro(String search){
        try {
            PageResponse<Venta> response = ventaService.obtenerVenta(search, paginaActual, tamanoPagina);
            List<Venta> ventas = (response != null && response.getContent() != null) ? response.getContent() : Collections.emptyList();
            ventasOriginales = ventas;
            ventasFiltradas = ventas;
            cargarVendedores();
            tblVentas.setItems(FXCollections.observableArrayList(ventas));
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
                    lblResumenPaginacion.setText("No hay ventas para mostrar");
                } else {
                    long desde = (long) pageNum * pageSize + 1;
                    long hasta = Math.min(desde + pageSize - 1, total);
                    lblResumenPaginacion.setText("Mostrando " + desde + "-" + hasta + " de " + total + " ventas (Página " + (pageNum + 1) + " de " + totalPaginas + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudieron obtener las ventas");
            alert.setContentText("Ocurrió un error al cargar el listado de ventas desde el servidor.");
            alert.showAndWait();
        }
    }

    @FXML
    private void ejecutarBusqueda() {
        obtenerVentas();
    }

    @FXML
    private void filtrarVentas() {
        aplicarFiltrosLocales();
    }

    private void cargarMetricas() {
        try {
            VentaMetrics metrics = ventaService.obtenerMetricas();
            if (metrics != null) {
                if (lblVendidoHoy != null) {
                    lblVendidoHoy.setText("S/ " + (metrics.getTodaySales() != null ? metrics.getTodaySales().setScale(2, RoundingMode.HALF_UP) : "0.00"));
                }
                if (lblTotalTransacciones != null) {
                    lblTotalTransacciones.setText(String.valueOf(metrics.getTransactions()));
                }
                if (lblTicketPromedio != null) {
                    lblTicketPromedio.setText("S/ " + (metrics.getAverageTicket() != null ? metrics.getAverageTicket().setScale(2, RoundingMode.HALF_UP) : "0.00"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void aplicarFiltrosLocales() {
        String vendedor = cbVendedor.getValue();
        String rangoFecha = cbRangoFecha.getValue();
        LocalDate hoy = LocalDate.now();
        List<Venta> resultado = ventasOriginales.stream().filter(v -> {
            if(vendedor == null || vendedor.equals("Todos")) return true;
            return v.getUser() != null && vendedor.equals(v.getUser().getUsername());
        }).filter(v -> {
            if(rangoFecha == null || rangoFecha.equals("Todas")) return true;
            if(v.getSaleDate() == null) return false;
            try {
                String dateStr = v.getSaleDate();
                LocalDate fechaVenta;
                if (dateStr.contains("T")) {
                    fechaVenta = LocalDateTime.parse(dateStr).toLocalDate();
                } else {
                    fechaVenta = LocalDate.parse(dateStr);
                }
                switch (rangoFecha) {
                    case "Hoy": return fechaVenta.equals(hoy);
                    case "Esta semana": return !fechaVenta.isBefore(hoy.with(DayOfWeek.MONDAY));
                    case "Este mes": return fechaVenta.getMonth() == hoy.getMonth() && fechaVenta.getYear() == hoy.getYear();
                    default: return true;
                }
            } catch (Exception e) {
                return false;
            }
        }).toList();

        ventasFiltradas = resultado;
        tblVentas.setItems(FXCollections.observableArrayList(resultado));
    }

    private void cargarVendedores() {
        String vendSelected = cbVendedor.getValue();
        cbVendedor.getItems().clear();
        cbVendedor.getItems().add("Todos");
        ventasOriginales.stream()
                .filter(v -> v.getUser() != null && v.getUser().getUsername() != null)
                .map(v -> v.getUser().getUsername())
                .distinct().sorted()
                .forEach(cbVendedor.getItems()::add);
        
        if (vendSelected != null && cbVendedor.getItems().contains(vendSelected)) {
            cbVendedor.setValue(vendSelected);
        } else {
            cbVendedor.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscarVenta.clear();
        cbVendedor.getSelectionModel().select("Todos");
        cbRangoFecha.getSelectionModel().select("Todas");
        obtenerVentas();
    }

    @FXML
    public void abrirModalNuevaVenta() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/ventas/ventas-form.fxml"));
        Parent root = loader.load();
        Stage modal = new Stage();
        com.store.inventario.utils.WindowUtils.applyIcon(modal);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Nueva Venta");
        modal.setScene(new Scene(root));
        modal.showAndWait();
        obtenerVentas();
    }
}


