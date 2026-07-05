package com.store.inventario.component;

import com.store.inventario.shared.model.NavigationManager;
import com.store.inventario.module.product.model.entity.ProductMetrics;
import com.store.inventario.module.product.service.ProductService;
import com.store.inventario.security.SessionManager;
import com.store.inventario.shared.utils.WindowUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class SidebarController {
    @FXML private HBox itemDashboard;
    @FXML private HBox itemProductos;
    @FXML private HBox itemCategorias;
    @FXML private HBox itemClientes;
    @FXML private HBox itemProveedores;
    @FXML private HBox itemCompras;
    @FXML private HBox itemVentas;
    @FXML private HBox itemGuias;
    @FXML private HBox itemEmpleados;
    @FXML private HBox itemUsuarios;
    @FXML private HBox itemAlertas;
    @FXML private HBox itemReportes;

    @FXML private Label lblBadgeAlertas;

    @FXML private VBox groupPrincipal;
    @FXML private VBox groupCatalogo;
    @FXML private VBox groupOperaciones;
    @FXML private VBox groupGestion;
    @FXML private VBox groupAnalisis;

    @FXML
    private void initialize() {
        String role = SessionManager.getInstance().getRole();

        if (role == null || !"ADMIN".equalsIgnoreCase(role.trim())) {
            ocultarComponente(itemDashboard);
            ocultarComponente(itemEmpleados);
            ocultarComponente(itemUsuarios);
        }

        if ("STOREKEEPER".equalsIgnoreCase(role)) {
            ocultarComponente(itemClientes);
            ocultarComponente(itemProveedores);
            ocultarComponente(itemCompras);
            ocultarComponente(itemVentas);
        }

        if ("SELLER".equalsIgnoreCase(role)) {
            ocultarComponente(itemGuias);
            ocultarComponente(itemProveedores);
            ocultarComponente(itemCompras);
        }

        actualizarVisibilidadGrupo(groupPrincipal, itemDashboard);
        actualizarVisibilidadGrupo(groupCatalogo, itemProductos, itemCategorias, itemProveedores, itemClientes);
        actualizarVisibilidadGrupo(groupOperaciones, itemCompras, itemVentas, itemGuias);
        actualizarVisibilidadGrupo(groupGestion, itemEmpleados, itemUsuarios);
        actualizarVisibilidadGrupo(groupAnalisis, itemAlertas, itemReportes);

        cargarBadgeAlertas();
        NavigationManager.getInstance().setOnRefreshAlerts(this::cargarBadgeAlertas);
    }

    private void ocultarComponente(HBox item) {
        if (item != null) {
            item.setVisible(false);
            item.setManaged(false);
        }
    }

    private void actualizarVisibilidadGrupo(VBox group, HBox... items) {
        boolean algunoVisible = false;
        for (HBox item : items) {
            if (item != null && item.isVisible()) {
                algunoVisible = true;
                break;
            }
        }
        if (group != null) {
            group.setVisible(algunoVisible);
            group.setManaged(algunoVisible);
        }
    }

    private void cargarBadgeAlertas() {
        new Thread(() -> {
            try {
                ProductService service = new ProductService();

                ProductMetrics metrics = service.obtenerMetricas();
                long count = (metrics != null) ? (metrics.getLowStockCount() + metrics.getOutOfStockCount()) : 0;

                javafx.application.Platform.runLater(() -> {
                    if (count > 0) {
                        lblBadgeAlertas.setText(String.valueOf(count));
                        lblBadgeAlertas.setVisible(true);
                        lblBadgeAlertas.setManaged(true);
                    } else {
                        lblBadgeAlertas.setVisible(false);
                        lblBadgeAlertas.setManaged(false);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleDashboard() {
        NavigationManager.getInstance().navegar("/views/component/dashboard.fxml");
    }

    @FXML
    private void handleProductos() {
        NavigationManager.getInstance().navegar("/views/product/product.fxml");
    }

    @FXML
    private void handleCategorias() {
        NavigationManager.getInstance().navegar("/views/product/category-management.fxml");
    }

    @FXML
    private void handleClientes() {
        NavigationManager.getInstance().navegar("/views/person/customer.fxml");
    }

    @FXML
    private void handleProveedores() {
        NavigationManager.getInstance().navegar("/views/supplier/supplier.fxml");
    }

    @FXML
    private void handleCompras() {
        NavigationManager.getInstance().navegar("/views/buy/purchase.fxml");
    }

    @FXML
    private void handleVentas() {
        NavigationManager.getInstance().navegar("/views/sale/sale.fxml");
    }

    @FXML
    private void handleGuias() {
        NavigationManager.getInstance().navegar("/views/movement/inventory-guide.fxml");
    }

    @FXML
    private void handleEmpleados() {
        NavigationManager.getInstance().navegar("/views/person/employee.fxml");
    }

    @FXML
    private void handleUsuarios() {
        NavigationManager.getInstance().navegar("/views/auth/user.fxml");
    }

    @FXML
    private void handleAlertas() {
        NavigationManager.getInstance().navegar("/views/component/alertas.fxml");
    }

    @FXML
    private void handleReportes() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Reportes");
        alert.setHeaderText(null);
        alert.setContentText("Falta todavia");
        com.store.inventario.shared.utils.WindowUtils.applyIcon(alert);
        alert.showAndWait();
    }



    @FXML
    private void handleLogout() {
        try {
            SessionManager.getInstance().cerrarSesion();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/auth/login.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            WindowUtils.applyIcon(loginStage);
            loginStage.setTitle("Sistema de Inventario");
            loginStage.setScene(new Scene(root));
            loginStage.setMinWidth(1280);
            loginStage.setMinHeight(800);
            loginStage.setMaximized(true);
            loginStage.show();

            Stage currentStage = (Stage) itemDashboard.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
