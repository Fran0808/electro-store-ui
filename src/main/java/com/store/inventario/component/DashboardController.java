package com.store.inventario.component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.store.inventario.module.buy.model.entity.PurchaseDashboard;
import com.store.inventario.module.buy.model.entity.PurchaseMetrics;
import com.store.inventario.module.buy.service.PucharseService;
import com.store.inventario.module.product.model.entity.Product;
import com.store.inventario.module.product.model.entity.ProductMetrics;
import com.store.inventario.module.product.service.ProductService;
import com.store.inventario.module.sale.model.entity.Sale;
import com.store.inventario.module.sale.model.entity.SaleMetrics;
import com.store.inventario.module.sale.service.SaleService;
import com.store.inventario.module.buy.model.entity.Purchase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

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

    @FXML private VBox vboxAlertasProgress;

    private final ProductService productService = new ProductService();
    private final SaleService saleService = new SaleService();
    private final PucharseService purchaseService = new PucharseService();


    private ProductMetrics productMetrics;
    private SaleMetrics saleMetrics;
    private PurchaseDashboard purchaseDashboard;
    private PurchaseMetrics purchaseMetrics;
    private List<Product> lowStockProducts = new ArrayList<>();

    private List<com.store.inventario.shared.model.DailySummary> dailySales = new ArrayList<>();
    private List<com.store.inventario.shared.model.DailySummary> dailyPurchases = new ArrayList<>();
    private List<com.store.inventario.shared.model.TopProduct> topProducts = new ArrayList<>();
    private List<com.store.inventario.shared.model.CategoryDistribution> categoryDistribution = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadApiData();
        loadFechaYKPIs();
        loadChartVentasCompras();
        loadChartTopProductos();
        loadChartCategorias();
        loadAlertasProgress();
    }


    private void loadApiData() {
        try {
            productMetrics = productService.obtenerMetricas();
        } catch (Exception e) {
            System.err.println("[Dashboard] Error al cargar métricas de productos: " + e.getMessage());
            productMetrics = null;
        }

        try {
            saleMetrics = saleService.obtenerMetricas();
        } catch (Exception e) {
            System.err.println("[Dashboard] Error al cargar métricas de ventas: " + e.getMessage());
            saleMetrics = null;
        }

        try {
            purchaseDashboard = purchaseService.obtenerDashboard();
        } catch (Exception e) {
            System.err.println("[Dashboard] Error al cargar dashboard de compras: " + e.getMessage());
            purchaseDashboard = null;
        }

        try {
            purchaseMetrics = purchaseService.obtenerMetricas();
        } catch (Exception e) {
            System.err.println("[Dashboard] Error al cargar métricas de compras: " + e.getMessage());
            purchaseMetrics = null;
        }

        try {
            lowStockProducts = productService.obtenerProductosStockBajo(10);
        } catch (Exception e) {
            System.err.println("[Dashboard] Error al cargar productos con stock bajo: " + e.getMessage());
            lowStockProducts = new ArrayList<>();
        }


        try {
            dailySales = saleService.obtenerResumenDiario(7);
        } catch (Exception e) {
            System.err.println("[Dashboard] Error al cargar resumen diario de ventas: " + e.getMessage());
            dailySales = new ArrayList<>();
        }

        try {
            dailyPurchases = purchaseService.obtenerResumenDiario(7);
        } catch (Exception e) {
            System.err.println("[Dashboard] Error al cargar resumen diario de compras: " + e.getMessage());
            dailyPurchases = new ArrayList<>();
        }


        try {
            topProducts = saleService.obtenerTopProductos(5);
        } catch (Exception e) {
            System.err.println("[Dashboard] Error al cargar productos más vendidos: " + e.getMessage());
            topProducts = new ArrayList<>();
        }


        try {
            categoryDistribution = productService.obtenerDistribucionCategorias();
        } catch (Exception e) {
            System.err.println("[Dashboard] Error al cargar distribución de categorías: " + e.getMessage());
            categoryDistribution = new ArrayList<>();
        }
    }

    private void loadFechaYKPIs() {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern(
                "d 'de' MMMM, yyyy", new Locale("es", "PE")
        );
        if (lblFechaResumen != null) {
            lblFechaResumen.setText("Resumen de operaciones · " + today.format(formatter));
        }

        if (lblValorInventario != null) {
            if (productMetrics != null) {
                lblValorInventario.setText(productMetrics.getTotalProducts() + " productos");
            } else {
                lblValorInventario.setText("—");
            }
        }

        if (lblVentasMes != null) {
            if (saleMetrics != null && saleMetrics.getTodaySales() != null) {
                lblVentasMes.setText("S/ " + formatMonto(saleMetrics.getTodaySales()));
            } else {
                lblVentasMes.setText("S/ 0.00");
            }
        }

        if (lblComprasMes != null) {
            if (purchaseDashboard != null && purchaseDashboard.getTodayPurchases() != null) {
                lblComprasMes.setText("S/ " + formatMonto(purchaseDashboard.getTodayPurchases()));
            } else {
                lblComprasMes.setText("S/ 0.00");
            }
        }

        if (lblStockCritico != null) {
            if (productMetrics != null) {
                long critico = productMetrics.getLowStockCount() + productMetrics.getOutOfStockCount();
                lblStockCritico.setText(critico + " productos");
            } else {
                lblStockCritico.setText("—");
            }
        }
    }

    private void loadChartVentasCompras() {
        chartVentasCompras.getData().clear();

        XYChart.Series<String, Number> seriesVentas = new XYChart.Series<>();
        seriesVentas.setName("Ventas");
        if (dailySales != null && !dailySales.isEmpty()) {
            for (com.store.inventario.shared.model.DailySummary item : dailySales) {

                String label = item.getDate().substring(Math.max(0, item.getDate().length() - 5)); // e.g. "07-08"
                seriesVentas.getData().add(new XYChart.Data<>(label, item.getTotal()));
            }
        } else {
            seriesVentas.getData().add(new XYChart.Data<>("Sin datos", 0));
        }

        XYChart.Series<String, Number> seriesCompras = new XYChart.Series<>();
        seriesCompras.setName("Compras");
        if (dailyPurchases != null && !dailyPurchases.isEmpty()) {
            for (com.store.inventario.shared.model.DailySummary item : dailyPurchases) {
                String label = item.getDate().substring(Math.max(0, item.getDate().length() - 5));
                seriesCompras.getData().add(new XYChart.Data<>(label, item.getTotal()));
            }
        } else {
            seriesCompras.getData().add(new XYChart.Data<>("Sin datos", 0));
        }

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
        if (topProducts != null && !topProducts.isEmpty()) {
            for (com.store.inventario.shared.model.TopProduct item : topProducts) {
                String shortName = item.getProductName().length() > 12 ? item.getProductName().substring(0, 10) + ".." : item.getProductName();
                series.getData().add(new XYChart.Data<>(shortName, item.getTotalQuantity()));
            }
        } else {
            series.getData().add(new XYChart.Data<>("Sin datos", 0));
        }

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

        if (categoryDistribution != null && !categoryDistribution.isEmpty()) {
            long total = categoryDistribution.stream().mapToLong(com.store.inventario.shared.model.CategoryDistribution::getCount).sum();
            for (com.store.inventario.shared.model.CategoryDistribution item : categoryDistribution) {
                double pct = total > 0 ? (double) item.getCount() / total * 100 : 0;
                String label = String.format("%s (%d%%)", item.getCategoryName(), (int) pct);
                chartCategorias.getData().add(new PieChart.Data(label, item.getCount()));
            }
        } else {
            chartCategorias.getData().add(new PieChart.Data("Sin datos", 1));
        }

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

        if (lowStockProducts.isEmpty()) {
            Label lblEmpty = new Label("No hay productos con stock bajo en este momento.");
            lblEmpty.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px; -fx-padding: 10 0 0 0;");
            vboxAlertasProgress.getChildren().add(lblEmpty);
            return;
        }

        for (Product product : lowStockProducts) {
            int stockActual = product.getStock() != null ? product.getStock() : 0;
            int stockMinimo = product.getLowStock() != null ? product.getLowStock() : 1;
            boolean isCritico = stockMinimo > 0 && stockActual <= (stockMinimo / 2.0);

            VBox row = new VBox(6);

            HBox topRow = new HBox();
            topRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label lblName = new Label(product.getName());
            lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #0F172A;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            Label lblBadge = new Label(isCritico ? "Crítico" : "Bajo");
            lblBadge.getStyleClass().add(isCritico ? "badge-critical" : "badge-low");

            topRow.getChildren().addAll(lblName, spacer, lblBadge);

            double progress = stockMinimo > 0 ? (double) stockActual / stockMinimo : 0.0;
            ProgressBar bar = new ProgressBar(Math.min(progress, 1.0));
            bar.setMaxWidth(Double.MAX_VALUE);
            bar.setPrefHeight(6);

            if (isCritico) {
                bar.setStyle("-fx-accent: #EF4444; -fx-control-inner-background: #F1F5F9;");
            } else {
                bar.setStyle("-fx-accent: #D97706; -fx-control-inner-background: #F1F5F9;");
            }

            Label lblMeta = new Label(stockActual + " / " + stockMinimo + " unidades (mín.)");
            lblMeta.setStyle("-fx-text-fill: #64748B; -fx-font-size: 11px;");

            row.getChildren().addAll(topRow, bar, lblMeta);
            vboxAlertasProgress.getChildren().add(row);
        }
    }



    private String formatMonto(BigDecimal monto) {
        if (monto == null) return "0.00";
        return String.format("%,.2f", monto);
    }
}
