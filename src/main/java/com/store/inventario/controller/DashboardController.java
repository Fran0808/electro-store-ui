package com.store.inventario.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class DashboardController implements Initializable {

    @FXML private Label lblValorInventario;
    @FXML private Label lblTotalProductos;
    @FXML private Label lblStockCritico;
    @FXML private Label lblRotacionPromedio;
    

    @FXML private LineChart<String, Number> chartVentasCompras;
    @FXML private BarChart<String, Number> chartTopProductos;
        @FXML private BarChart<String, Number> chartStockCategoria;
    @FXML private AreaChart<String, Number> chartRotacion;

    @FXML private ComboBox<String> cbPeriodoVentas;
    @FXML private ComboBox<String> cbPeriodoProductos;
    @FXML private ComboBox<String> cbPeriodoRotacion;
    

    @FXML private TableView<AlertaCritica> tblAlertasCriticas;
    @FXML private TableColumn<AlertaCritica, String> colProducto;
    @FXML private TableColumn<AlertaCritica, Integer> colStock;
    @FXML private TableColumn<AlertaCritica, Integer> colLimite;
    @FXML private TableColumn<AlertaCritica, String> colProveedor;
    @FXML private TableColumn<AlertaCritica, String> colAccion;

    

    @FXML private Button btnVerTodasAlertas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeCombos();
                loadKPIs();
                if (tblAlertasCriticas != null) {
                        tblAlertasCriticas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                }
                loadChartVentasCompras();
                loadChartTopProductos();
                loadChartStockCategoria();
                loadChartRotacion();
                loadAlertasCriticas();
    }
    

    private void initializeCombos() {
        cbPeriodoVentas.setItems(FXCollections.observableArrayList(
                "Últimos 7 días", "Últimos 14 días", "Último mes", "Últimos 3 meses"
        ));
        cbPeriodoVentas.setValue("Últimos 14 días");

        cbPeriodoProductos.setItems(FXCollections.observableArrayList(
                "Último mes", "Últimos 3 meses", "Último año"
        ));
        cbPeriodoProductos.setValue("Último mes");

        cbPeriodoRotacion.setItems(FXCollections.observableArrayList(
                "Últimas 2 semanas", "Últimos 30 días", "Últimos 90 días"
        ));
        cbPeriodoRotacion.setValue("Últimas 2 semanas");

    }

    private void loadKPIs() {
        // TODO: Conectar con base de datos real
                if (lblValorInventario != null) lblValorInventario.setText("S/ 125,450.00");
                if (lblTotalProductos != null) lblTotalProductos.setText("1,284");
                if (lblStockCritico != null) lblStockCritico.setText("42");
                if (lblRotacionPromedio != null) lblRotacionPromedio.setText("2.4x");
    }

    private void loadChartVentasCompras() {
        // TODO: Conectar con base de datos real
        chartVentasCompras.getData().clear();

        XYChart.Series<String, Number> seriesVentas = new XYChart.Series<>();
        seriesVentas.setName("Ventas");
        seriesVentas.getData().addAll(
                new XYChart.Data<>("Día 1", 450),
                new XYChart.Data<>("Día 2", 520),
                new XYChart.Data<>("Día 3", 480),
                new XYChart.Data<>("Día 4", 650),
                new XYChart.Data<>("Día 5", 720),
                new XYChart.Data<>("Día 6", 680),
                new XYChart.Data<>("Día 7", 750)
        );

        XYChart.Series<String, Number> seriesCompras = new XYChart.Series<>();
        seriesCompras.setName("Compras");
        seriesCompras.getData().addAll(
                new XYChart.Data<>("Día 1", 300),
                new XYChart.Data<>("Día 2", 280),
                new XYChart.Data<>("Día 3", 350),
                new XYChart.Data<>("Día 4", 400),
                new XYChart.Data<>("Día 5", 380),
                new XYChart.Data<>("Día 6", 420),
                new XYChart.Data<>("Día 7", 400)
        );

        chartVentasCompras.getData().addAll(seriesVentas, seriesCompras);
    }

    private void loadChartTopProductos() {
        // TODO: Conectar con base de datos real
        chartTopProductos.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Unidades Vendidas");
        series.getData().addAll(
                new XYChart.Data<>("Refrigerador", 350),
                new XYChart.Data<>("Lavadora", 320),
                new XYChart.Data<>("Microondas", 280),
                new XYChart.Data<>("Televisor", 240),
                new XYChart.Data<>("Secadora", 200)
        );

        chartTopProductos.getData().add(series);
    }

    private void loadChartStockCategoria() {
        if (chartStockCategoria == null) return;
        chartStockCategoria.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Stock por categoría");
        series.getData().addAll(
                new XYChart.Data<>("Electrodomésticos Grandes", 280),
                new XYChart.Data<>("Electrónica", 150),
                new XYChart.Data<>("Pequeños Electrodomésticos", 420),
                new XYChart.Data<>("Accesorios", 240),
                new XYChart.Data<>("Otros", 194)
        );
        chartStockCategoria.getData().add(series);
                javafx.application.Platform.runLater(() -> {
                        String[] colors = new String[]{"#F97316", "#F59E0B", "#10B981", "#3B82F6", "#6366F1"};
                        for (int i = 0; i < series.getData().size(); i++) {
                                XYChart.Data<String, Number> d = series.getData().get(i);
                                if (d.getNode() != null) {
                                        d.getNode().setStyle("-fx-bar-fill: " + colors[i % colors.length] + ";");
                                }
                        }
                });
    }

    private void loadChartRotacion() {
        chartRotacion.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Salidas (unidades)");
        series.getData().addAll(
                new XYChart.Data<>("Día 1", 120),
                new XYChart.Data<>("Día 2", 150),
                new XYChart.Data<>("Día 3", 180),
                new XYChart.Data<>("Día 4", 165),
                new XYChart.Data<>("Día 5", 220),
                new XYChart.Data<>("Día 6", 200),
                new XYChart.Data<>("Día 7", 250)
        );

        chartRotacion.getData().add(series);
    }

    private void loadAlertasCriticas() {
        ObservableList<AlertaCritica> data = FXCollections.observableArrayList();

        data.add(new AlertaCritica("Refrigerador 28p Samsung", 5, 20, "Samsung Electronics", "Reordenar"));
        data.add(new AlertaCritica("Lavadora 8kg LG", 3, 15, "LG Electronics", "Reordenar"));
        data.add(new AlertaCritica("Microondas 25L Whirlpool", 2, 10, "Whirlpool", "Crítico"));
        data.add(new AlertaCritica("TV 55\" LG Smart", 0, 5, "LG Electronics", "Agotado"));
        data.add(new AlertaCritica("Secadora 6kg Electrolux", 4, 12, "Electrolux", "Reordenar"));

        colProducto.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProducto()));
        colStock.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getStock()).asObject());
        colLimite.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getLimite()).asObject());
        colProveedor.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProveedor()));
        colAccion.setCellValueFactory(cellData -> 
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAccion()));

        tblAlertasCriticas.setItems(data);
    }

    public static class AlertaCritica {
        private String producto;
        private int stock;
        private int limite;
        private String proveedor;
        private String accion;

        public AlertaCritica(String producto, int stock, int limite, String proveedor, String accion) {
            this.producto = producto;
            this.stock = stock;
            this.limite = limite;
            this.proveedor = proveedor;
            this.accion = accion;
        }

        public String getProducto() { return producto; }
        public int getStock() { return stock; }
        public int getLimite() { return limite; }
        public String getProveedor() { return proveedor; }
        public String getAccion() { return accion; }
    }
}
