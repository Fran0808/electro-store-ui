package com.store.inventario;

import com.store.inventario.security.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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

        if ("RECEPTION".equalsIgnoreCase(role)) {
            ocultarComponente(itemGuias);
            ocultarComponente(itemProveedores);
            ocultarComponente(itemCompras);
        }

        actualizarVisibilidadGrupo(groupPrincipal, itemDashboard);
        actualizarVisibilidadGrupo(groupCatalogo, itemProductos, itemCategorias, itemProveedores, itemClientes);
        actualizarVisibilidadGrupo(groupOperaciones, itemCompras, itemVentas, itemGuias);
        actualizarVisibilidadGrupo(groupGestion, itemEmpleados, itemUsuarios);
        actualizarVisibilidadGrupo(groupAnalisis, itemAlertas);

        cargarBadgeAlertas();
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
                com.store.inventario.service.producto.ProductoService service = new com.store.inventario.service.producto.ProductoService();
                com.store.inventario.model.PageResponse<com.store.inventario.model.producto.Producto> response = service.obtenerProductos(0, 1000);
                long count = response.getContent().stream()
                        .filter(p -> {
                            int stock = p.getStock() != null ? p.getStock() : 0;
                            int limit = p.getLowStock() != null ? p.getLowStock() : 5;
                            return stock <= limit;
                        })
                        .count();
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
                System.err.println("No se pudo cargar el badge de alertas en el sidebar: " + e.getMessage());
                javafx.application.Platform.runLater(() -> {
                    lblBadgeAlertas.setVisible(false);
                    lblBadgeAlertas.setManaged(false);
                });
            }
        }).start();
    }

    @FXML
    private void handleDashboard() {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/dashboard.fxml");
    }

    @FXML
    private void handleProductos() {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/productos/productos.fxml");
    }

    @FXML
    private void handleCategorias() {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/productos/gestion-categorias.fxml");
    }

    @FXML
    private void handleClientes() {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/clientes/clientes.fxml");
    }

    @FXML
    private void handleProveedores() {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/proveedores/proveedores.fxml");
    }

    @FXML
    private void handleCompras() {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/compras/compras.fxml");
    }

    @FXML
    private void handleVentas() {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/ventas/ventas.fxml");
    }

    @FXML
    private void handleGuias() {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/inventario/guias/guias.fxml");
    }

    @FXML
    private void handleEmpleados() {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/empleados/empleados.fxml");
    }

    @FXML
    private void handleUsuarios() {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/usuarios/usuarios.fxml");
    }

    @FXML
    private void handleAlertas() {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/inventario/alertas/alertas.fxml");
    }



    @FXML
    private void handleLogout() {
        try {
            SessionManager.getInstance().cerrarSesion();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/login.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Sistema de Inventario");
            loginStage.setScene(new Scene(root));
            loginStage.setMinWidth(1280);
            loginStage.setMinHeight(800);

            try {
                Image icon = new Image(getClass().getResourceAsStream("/logo.png"));
                loginStage.getIcons().add(icon);
            } catch (Exception e) {
                System.err.println("No se pudo cargar el logo de la aplicación: " + e.getMessage());
            }

            loginStage.show();

            Stage currentStage = (Stage) itemDashboard.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
