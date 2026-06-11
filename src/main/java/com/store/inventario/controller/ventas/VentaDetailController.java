package com.store.inventario.controller.ventas;

import com.store.inventario.model.producto.Producto;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class VentaDetailController {
    @FXML private TableView<Producto> tblProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colProducto;
    @FXML private TableColumn<Producto, Integer> colCantidad;
    @FXML private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML private TableColumn<Producto, BigDecimal> colSubtotal;
    @FXML private Label lblTotalArticulos;
    @FXML private Label lblTotalVenta;
    @FXML private Label lblVendedor;
    @FXML private Label lblCliente;
    @FXML private Label lblClienteId;
    @FXML private Label lblFechaHora;
    @FXML private Label lblCodigo;
    @FXML private Button btnCerrar, btnExportar;

    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleExportar() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Exportar");
        alert.setHeaderText(null);
        alert.setContentText("Guía exportada correctamente");
        alert.showAndWait();
    }
}
