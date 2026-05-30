package com.store.inventario;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

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
        NavigationManager.getInstance().navegar("/com/store/inventario/views/inventario/usuarios/usuarios.fxml");
    }
}
