package com.store.inventario.controller.alertas;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.producto.Producto;
import com.store.inventario.service.producto.ProductoService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AlertaController implements Initializable {

    @FXML private TableView<AlertaItem> tblAlertas;
    @FXML private TableColumn<AlertaItem, String> colCodigo;
    @FXML private TableColumn<AlertaItem, String> colProveedor;
    @FXML private TableColumn<AlertaItem, String> colProducto;
    @FXML private TableColumn<AlertaItem, Integer> colStock;
    @FXML private TableColumn<AlertaItem, Integer> colLimite;
    @FXML private TableColumn<AlertaItem, String> colPrioridad;
    @FXML private TableColumn<AlertaItem, String> colEstado;

    @FXML private Button btnConfigLimite;
    @FXML private Label lblResumenPaginacion;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;

    private final ProductoService productoService = new ProductoService();
    private final List<AlertaItem> todasLasAlertas = new ArrayList<>();
    private final List<Producto> listaProductosOriginales = new ArrayList<>();
    private int paginaActual = 0;
    private final int tamanoPagina = 30;

    public static int globalAlertLimit = 5;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnas();
        cargarAlertas();

        btnAnterior.setOnAction(e -> handleAnterior());
        btnSiguiente.setOnAction(e -> handleSiguiente());
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCodigo()));
        colProveedor.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProveedor()));
        colProducto.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProducto()));
        colStock.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getStock()).asObject());
        colLimite.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getLimite()).asObject());
        colPrioridad.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPrioridad()));
        colEstado.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEstado()));
    }

    private void cargarAlertas() {
        try {
            PageResponse<Producto> response = productoService.obtenerProductos(0, 1000);
            List<Producto> productos = response.getContent();
            listaProductosOriginales.clear();
            listaProductosOriginales.addAll(productos);
            todasLasAlertas.clear();

            for (Producto p : productos) {
                int stock = p.getStock() != null ? p.getStock() : 0;
                int lowStockLimit = p.getLowStock() != null ? p.getLowStock() : globalAlertLimit;

                if (stock <= lowStockLimit) {
                    String proveedor = p.getBrand() != null && !p.getBrand().isEmpty() ? p.getBrand() : "General";

                    StringBuilder nameBuilder = new StringBuilder(p.getName());
                    if (p.getBrand() != null && !p.getBrand().isEmpty()) {
                        nameBuilder.append(" ").append(p.getBrand());
                    }
                    if (p.getModel() != null && !p.getModel().isEmpty()) {
                        nameBuilder.append(" ").append(p.getModel());
                    }

                    String prioridad;
                    String estado;
                    if (stock == 0) {
                        prioridad = "ALTA";
                        estado = "Agotado";
                    } else if (stock <= lowStockLimit / 2) {
                        prioridad = "MEDIA";
                        estado = "Stock Bajo";
                    } else {
                        prioridad = "BAJA";
                        estado = "Reordenar";
                    }

                    todasLasAlertas.add(new AlertaItem(
                            p.getCode(),
                            proveedor,
                            nameBuilder.toString(),
                            stock,
                            lowStockLimit,
                            prioridad,
                            estado
                    ));
                }
            }

            paginaActual = 0;
            actualizarTablaYPaginacion();

        } catch (Exception e) {
            System.err.println("No se pudieron cargar las alertas de la API, usando mock data: " + e.getMessage());
            cargarAlertasMock();
        }
    }

    private void cargarAlertasMock() {
        todasLasAlertas.clear();
        todasLasAlertas.add(new AlertaItem("PROD001", "Samsung", "Refrigerador 28p Samsung RF28", 5, 20, "MEDIA", "Reordenar"));
        todasLasAlertas.add(new AlertaItem("PROD002", "LG", "Lavadora 8kg LG WT8", 3, 15, "MEDIA", "Reordenar"));
        todasLasAlertas.add(new AlertaItem("PROD003", "Whirlpool", "Microondas 25L Whirlpool WM25", 2, 10, "ALTA", "Stock Bajo"));
        todasLasAlertas.add(new AlertaItem("PROD004", "LG", "TV 55\" LG Smart 55UQ", 0, 5, "ALTA", "Agotado"));
        todasLasAlertas.add(new AlertaItem("PROD005", "Electrolux", "Secadora 6kg Electrolux ED6", 4, 12, "BAJA", "Reordenar"));

        paginaActual = 0;
        actualizarTablaYPaginacion();
    }

    private void actualizarTablaYPaginacion() {
        int total = todasLasAlertas.size();

        if (total == 0) {
            tblAlertas.setItems(FXCollections.observableArrayList());
            lblResumenPaginacion.setText("No hay alertas de stock para mostrar");
            btnAnterior.setDisable(true);
            btnSiguiente.setDisable(true);
            return;
        }

        int paginas = (int) Math.ceil((double) total / tamanoPagina);
        if (paginaActual >= paginas) {
            paginaActual = paginas - 1;
        }
        if (paginaActual < 0) {
            paginaActual = 0;
        }

        int desde = paginaActual * tamanoPagina;
        int hasta = Math.min(desde + tamanoPagina, total);

        List<AlertaItem> subLista = todasLasAlertas.subList(desde, hasta);
        tblAlertas.setItems(FXCollections.observableArrayList(subLista));

        btnAnterior.setDisable(paginaActual == 0);
        btnSiguiente.setDisable(paginaActual >= paginas - 1);

        lblResumenPaginacion.setText("Mostrando " + (desde + 1) + "-" + hasta + " de " + total + " alertas (Página " + (paginaActual + 1) + " de " + paginas + ")");
    }

    private void handleAnterior() {
        if (paginaActual > 0) {
            paginaActual--;
            actualizarTablaYPaginacion();
        }
    }

    private void handleSiguiente() {
        int total = todasLasAlertas.size();
        int paginas = (int) Math.ceil((double) total / tamanoPagina);
        if (paginaActual < paginas - 1) {
            paginaActual++;
            actualizarTablaYPaginacion();
        }
    }

    @FXML
    private void handleForm() throws IOException {
        AlertaItem selectedItem = tblAlertas.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Ningún producto seleccionado");
            alert.setContentText("Por favor, seleccione un producto de la tabla para configurar su límite.");
            alert.showAndWait();
            return;
        }

        Producto selectedProduct = listaProductosOriginales.stream()
                .filter(p -> p.getCode().equals(selectedItem.getCodigo()))
                .findFirst()
                .orElse(null);

        if (selectedProduct == null) {
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/alertas/alertas-form.fxml"));
        Parent root = loader.load();

        AlertasFormController formController = loader.getController();
        formController.setProducto(selectedProduct);

        Stage modal = new Stage();
        com.store.inventario.utils.WindowUtils.applyIcon(modal);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Editar límite de alerta - " + selectedProduct.getName());
        modal.setScene(new Scene(root));
        modal.showAndWait();

        cargarAlertas();
    }

    public static class AlertaItem {
        private final String codigo;
        private final String proveedor;
        private final String producto;
        private final int stock;
        private final int limite;
        private final String prioridad;
        private final String estado;

        public AlertaItem(String codigo, String proveedor, String producto, int stock, int limite, String prioridad, String estado) {
            this.codigo = codigo;
            this.proveedor = proveedor;
            this.producto = producto;
            this.stock = stock;
            this.limite = limite;
            this.prioridad = prioridad;
            this.estado = estado;
        }

        public String getCodigo() { return codigo; }
        public String getProveedor() { return proveedor; }
        public String getProducto() { return producto; }
        public int getStock() { return stock; }
        public int getLimite() { return limite; }
        public String getPrioridad() { return prioridad; }
        public String getEstado() { return estado; }
    }
}
