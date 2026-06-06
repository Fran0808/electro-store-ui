package com.store.inventario;

import com.store.inventario.security.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class SidebarController {
    @FXML
    private HBox itemDashboard;
    @FXML
    private HBox itemProductos;
    @FXML
    private HBox itemClientes;
    @FXML
    private HBox itemProveedores;
    @FXML
    private HBox itemCompras;
    @FXML
    private HBox itemVentas;
    @FXML
    private HBox itemInventario;
    @FXML
    private VBox subMenuInventario;
    @FXML
    private HBox itemEmpleados;
    @FXML
    private HBox itemUsuarios;

    @FXML
    private void initialize() {
        String role = SessionManager.getInstance().getRole();

        if (role == null || !"ADMIN".equalsIgnoreCase(role.trim())) {
            if(itemDashboard != null) {
                itemDashboard.setVisible(false);
                itemDashboard.setManaged(false);
            }
            if (itemEmpleados != null) {
                itemEmpleados.setVisible(false);
                itemEmpleados.setManaged(false);
            }
            if (itemUsuarios != null) {
                itemUsuarios.setVisible(false);
                itemUsuarios.setManaged(false);
            }
        }

        if("STOREKEEPER".equalsIgnoreCase(role)) {
            ocultarComponente(itemClientes);
            ocultarComponente(itemProveedores);
            ocultarComponente(itemCompras);
            ocultarComponente(itemVentas);
        }
        if("RECEPTION".equalsIgnoreCase(role)) {
            ocultarComponente(itemInventario);
            ocultarComponente(itemProveedores);
            ocultarComponente(itemCompras);
            if (subMenuInventario != null) {
                subMenuInventario.setVisible(false);
                subMenuInventario.setManaged(false);
            }
        }
    }

    private void ocultarComponente(HBox item){
        if(item != null){
            item.setVisible(false);
            item.setManaged(false);
        }
    }
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

            Stage currentStage = (Stage) subMenuInventario.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
