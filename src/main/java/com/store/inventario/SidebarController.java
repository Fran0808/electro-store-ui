package com.store.inventario;

import com.store.inventario.security.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class SidebarController {
    @FXML
    private void handleDashboard(){
        NavigationManager.getInstance().navegar("/com/store/inventario/views/dashboard.fxml");
    }
    @FXML
    private void handleProductos(){
        NavigationManager.getInstance().navegar("/com/store/inventario/views/productos/productos.fxml");
    }
    @FXML
    private void handleClientes(){
        NavigationManager.getInstance().navegar("/com/store/inventario/views/clientes/clientes.fxml");
    }
    @FXML
    private void handleProveedores(){
        NavigationManager.getInstance().navegar("/com/store/inventario/views/proveedores/proveedores.fxml");
    }
    @FXML
    private void handleCompras(){
        NavigationManager.getInstance().navegar("/com/store/inventario/views/compras/compras.fxml");
    }
    @FXML
    private void handleVentas(){
        NavigationManager.getInstance().navegar("/com/store/inventario/views/ventas/ventas.fxml");
    }
    @FXML
    private void handleEmpleados(){
        NavigationManager.getInstance().navegar("/com/store/inventario/views/empleados/empleados.fxml");
    }
    @FXML private VBox subMenuInventario;

    @FXML
    private void handleInventario() {
        subMenuInventario.setVisible(!subMenuInventario.isVisible());
        subMenuInventario.setManaged(!subMenuInventario.isManaged());
    }

    @FXML
    private void handleAlertas() {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/inventario/alertas/alertas.fxml");
    }

    @FXML
    private void handleMovimientos() {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/inventario/movimientos/movimiento-inventario.fxml");
    }

    @FXML
    private void handleGuias() {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/inventario/guias/guias.fxml");
    }

    @FXML
    private void handleUsuarios() {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/usuarios/usuarios.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            // Cerrar la sesión en SessionManager
            SessionManager.getInstance().cerrarSesion();

            // Cargar la vista de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/login.fxml"));
            Parent root = loader.load();

            // Crear y mostrar el Stage de login
            Stage loginStage = new Stage();
            loginStage.setTitle("Sistema de Inventario");
            loginStage.setScene(new Scene(root));
            loginStage.setMinWidth(1280);
            loginStage.setMinHeight(800);
            loginStage.show();

            // Cerrar el Stage actual (del sidebar/layout principal)
            Stage currentStage = (Stage) subMenuInventario.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
