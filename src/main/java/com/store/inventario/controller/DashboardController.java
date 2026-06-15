package com.store.inventario.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

public class DashboardController implements Initializable {

    @FXML private Label lblValorInventario;
    @FXML private Label lblVentasMes;
    @FXML private Label lblComprasMes;
    @FXML private Label lblStockCritico;
    @FXML private Label lblFechaResumen;

    @FXML private LineChart<String, Number> chartVentasCompras;
    @FXML private BarChart<String, Number> chartTopProductos;
    @FXML private PieChart chartCategorias;

    @FXML private ComboBox<String> cbPeriodoVentas;
    @FXML private ComboBox<String> cbPeriodoProductos;

    @FXML private VBox vboxAlertasProgress;

    @FXML private TableView<MovimientoReciente> tblActividad;
    @FXML private TableColumn<MovimientoReciente, MovimientoReciente> colMovimiento;
    @FXML private TableColumn<MovimientoReciente, String> colContraparte;
    @FXML private TableColumn<MovimientoReciente, String> colMonto;
    @FXML private TableColumn<MovimientoReciente, String> colFecha;
    @FXML private TableColumn<MovimientoReciente, String> colEstado;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeCombos();
        loadFechaYKPIs();
        loadChartVentasCompras();
        loadChartTopProductos();
        loadChartCategorias();
        loadAlertasProgress();
        loadActividadReciente();
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
    }

    private void loadFechaYKPIs() {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(
                "d 'de' MMMM, yyyy", new java.util.Locale("es", "PE")
        );
        if (lblFechaResumen != null) {
            lblFechaResumen.setText("Resumen de operaciones · " + today.format(formatter));
        }

        if (lblValorInventario != null) lblValorInventario.setText("S/ 487,250");
        if (lblVentasMes != null) lblVentasMes.setText("S/ 86,420");
        if (lblComprasMes != null) lblComprasMes.setText("S/ 52,180");
        if (lblStockCritico != null) lblStockCritico.setText("8 productos");
    }

    private void loadChartVentasCompras() {
        chartVentasCompras.getData().clear();

        XYChart.Series<String, Number> seriesVentas = new XYChart.Series<>();
        seriesVentas.setName("Ventas");
        seriesVentas.getData().addAll(
                new XYChart.Data<>("Ene", 62000),
                new XYChart.Data<>("Feb", 71000),
                new XYChart.Data<>("Mar", 68500),
                new XYChart.Data<>("Abr", 79200),
                new XYChart.Data<>("May", 91300),
                new XYChart.Data<>("Jun", 86420)
        );

        XYChart.Series<String, Number> seriesCompras = new XYChart.Series<>();
        seriesCompras.setName("Compras");
        seriesCompras.getData().addAll(
                new XYChart.Data<>("Ene", 45000),
                new XYChart.Data<>("Feb", 50200),
                new XYChart.Data<>("Mar", 48100),
                new XYChart.Data<>("Abr", 55300),
                new XYChart.Data<>("May", 60500),
                new XYChart.Data<>("Jun", 52180)
        );

        chartVentasCompras.getData().addAll(seriesVentas, seriesCompras);

        javafx.application.Platform.runLater(() -> {
            if (seriesVentas.getNode() != null) {
                seriesVentas.getNode().setStyle("-fx-stroke: #D97706;");
            }
            if (seriesCompras.getNode() != null) {
                seriesCompras.getNode().setStyle("-fx-stroke: #0D9488;");
            }
        });
    }

    private void loadChartTopProductos() {
        chartTopProductos.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Unidades Vendidas");
        series.getData().addAll(
                new XYChart.Data<>("Refri Samsung", 42),
                new XYChart.Data<>("Lava LG", 38),
                new XYChart.Data<>("Microondas", 35),
                new XYChart.Data<>("Aire Split", 29),
                new XYChart.Data<>("Licuadora", 27)
        );

        chartTopProductos.getData().add(series);
        
        javafx.application.Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-bar-fill: #0D9488;");
                }
            }
        });
    }

    private void loadChartCategorias() {
        chartCategorias.getData().clear();

        chartCategorias.getData().addAll(
                new PieChart.Data("Refrigeración (28%)", 28),
                new PieChart.Data("Lavado y secado (22%)", 22),
                new PieChart.Data("Climatización (18%)", 18),
                new PieChart.Data("Cocina (16%)", 16),
                new PieChart.Data("TV y audio (10%)", 10),
                new PieChart.Data("Pequeños electrod. (6%)", 6)
        );

        javafx.application.Platform.runLater(() -> {
            String[] colors = new String[]{"#D97706", "#0D9488", "#3B82F6", "#8B5CF6", "#EF4444", "#64748B"};
            int i = 0;
            for (PieChart.Data data : chartCategorias.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-pie-color: " + colors[i % colors.length] + ";");
                }
                i++;
            }
        });
    }

    private void loadAlertasProgress() {
        vboxAlertasProgress.getChildren().clear();

        List<AlertaItem> list = List.of(
                new AlertaItem("Plancha a vapor ProSteam", 3, 10, true),
                new AlertaItem("Cafetera programable 1.5L", 4, 12, true),
                new AlertaItem("Aspiradora ciclónica 1400W", 9, 20, true),
                new AlertaItem("Horno eléctrico 45L", 5, 10, false),
                new AlertaItem("Ventilador de torre 40\"", 8, 15, false)
        );

        for (AlertaItem item : list) {
            VBox row = new VBox(6);
            
            HBox topRow = new HBox();
            topRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            
            Label lblName = new Label(item.nombre);
            lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #0F172A;");
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            
            Label lblBadge = new Label(item.isCritico ? "Crítico" : "Bajo");
            lblBadge.getStyleClass().add(item.isCritico ? "badge-critical" : "badge-low");
            
            topRow.getChildren().addAll(lblName, spacer, lblBadge);
            
            ProgressBar bar = new ProgressBar((double) item.actual / item.limite);
            bar.setMaxWidth(Double.MAX_VALUE);
            bar.setPrefHeight(6);
            

            if (item.isCritico) {
                bar.setStyle("-fx-accent: #EF4444; -fx-control-inner-background: #F1F5F9;");
            } else {
                bar.setStyle("-fx-accent: #D97706; -fx-control-inner-background: #F1F5F9;");
            }
            
            Label lblMeta = new Label(item.actual + " / " + item.limite + " unidades");
            lblMeta.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");
            
            row.getChildren().addAll(topRow, bar, lblMeta);
            vboxAlertasProgress.getChildren().add(row);
        }
    }

    private void loadActividadReciente() {
        ObservableList<MovimientoReciente> data = FXCollections.observableArrayList(
                new MovimientoReciente("Venta #V-2456", "Refrigeradora Inverter 300L ×1", "J. Mendoza", "S/ 2,150.00", "Hoy, 10:24", "Completado"),
                new MovimientoReciente("Compra #C-0892", "Microondas digital 23L ×20", "Electro Import S.A.C.", "S/ 8,400.00", "Hoy, 09:10", "En tránsito"),
                new MovimientoReciente("Venta #V-2455", "Licuadora 3 velocidades ×2", "M. Torres", "S/ 240.00", "Hoy, 08:55", "Completado"),
                new MovimientoReciente("Guía #G-1033", "Salida de almacén → Tienda Surco · 15 prod", "Tienda Surco", "—", "Ayer, 17:40", "Enviado"),
                new MovimientoReciente("Compra #C-0891", "Aire acondicionado Split 12000 BTU ×10", "Hogar Tech Perú", "S/ 35,000.00", "Ayer, 14:20", "Pendiente")
        );

        colMovimiento.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue()));
        colContraparte.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getContraparte()));
        colMonto.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMonto()));
        colFecha.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFecha()));
        colEstado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstado()));

        colMovimiento.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(MovimientoReciente item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    HBox cellBox = new HBox(10);
                    cellBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    StackPane iconContainer = new StackPane();
                    iconContainer.setPrefSize(30, 30);
                    iconContainer.setMinSize(30, 30);
                    iconContainer.setMaxSize(30, 30);
                    
                    SVGPath svg = new SVGPath();
                    svg.setScaleX(1.1);
                    svg.setScaleY(1.1);

                    if (item.getTitulo().startsWith("Venta")) {
                        svg.setContent("M3 17l6-6 4 4 8-8M15 7h6v6");
                        svg.setStyle("-fx-fill: transparent; -fx-stroke: #0D9488; -fx-stroke-width: 2px;");
                        iconContainer.setStyle("-fx-background-color: #F0FDFA; -fx-background-radius: 8px;");
                    } else if (item.getTitulo().startsWith("Compra")) {
                        svg.setContent("M19 6h-2c0-2.76-2.24-5-5-5S7 3.24 7 6H5c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm-7-3c1.66 0 3 1.34 3 3H9c0-1.66 1.34-3 3-3zm7 17H5V8h14v12z");
                        svg.setStyle("-fx-fill: #D97706;");
                        iconContainer.setStyle("-fx-background-color: #FFFBEB; -fx-background-radius: 8px;");
                    } else {
                        svg.setContent("M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z");
                        svg.setStyle("-fx-fill: #64748B;");
                        iconContainer.setStyle("-fx-background-color: #F1F5F9; -fx-background-radius: 8px;");
                    }
                    iconContainer.getChildren().add(svg);

                    VBox textBox = new VBox(2);
                    Label lblTitle = new Label(item.getTitulo());
                    lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #0F172A;");
                    
                    Label lblSub = new Label(item.getSubtitulo());
                    lblSub.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");
                    
                    textBox.getChildren().addAll(lblTitle, lblSub);
                    cellBox.getChildren().addAll(iconContainer, textBox);
                    setGraphic(cellBox);
                }
            }
        });

        colEstado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label lblStatus = new Label(item);
                    if ("Completado".equalsIgnoreCase(item)) {
                        lblStatus.setStyle("-fx-background-color: rgba(74,222,128,0.15); -fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-padding: 3 10 3 10; -fx-background-radius: 7px;");
                    } else if ("En tránsito".equalsIgnoreCase(item)) {
                        lblStatus.setStyle("-fx-background-color: #F0FDFA; -fx-text-fill: #0D9488; -fx-font-weight: bold; -fx-padding: 3 10 3 10; -fx-background-radius: 7px;");
                    } else if ("Pendiente".equalsIgnoreCase(item)) {
                        lblStatus.setStyle("-fx-background-color: #FFFBEB; -fx-text-fill: #D97706; -fx-font-weight: bold; -fx-padding: 3 10 3 10; -fx-background-radius: 7px;");
                    } else {
                        lblStatus.setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #64748B; -fx-font-weight: bold; -fx-padding: 3 10 3 10; -fx-background-radius: 7px;");
                    }
                    setGraphic(lblStatus);
                }
            }
        });

        tblActividad.setItems(data);
    }

    private static class AlertaItem {
        String nombre;
        int actual;
        int limite;
        boolean isCritico;

        AlertaItem(String nombre, int actual, int limite, boolean isCritico) {
            this.nombre = nombre;
            this.actual = actual;
            this.limite = limite;
            this.isCritico = isCritico;
        }
    }

    public static class MovimientoReciente {
        private String titulo;
        private String subtitulo;
        private String contraparte;
        private String monto;
        private String fecha;
        private String estado;

        public MovimientoReciente(String titulo, String subtitulo, String contraparte, String monto, String fecha, String estado) {
            this.titulo = titulo;
            this.subtitulo = subtitulo;
            this.contraparte = contraparte;
            this.monto = monto;
            this.fecha = fecha;
            this.estado = estado;
        }

        public String getTitulo() { return titulo; }
        public String getSubtitulo() { return subtitulo; }
        public String getContraparte() { return contraparte; }
        public String getMonto() { return monto; }
        public String getFecha() { return fecha; }
        public String getEstado() { return estado; }
    }
}
