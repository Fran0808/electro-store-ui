package com.store.inventario.controller.ventas;

import com.store.inventario.model.ventas.Venta;
import com.store.inventario.model.ventas.VentaDetalle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class VentaDetailController {
    @FXML private TableView<VentaDetalle> tblProductos;
    @FXML private Label lblTotalArticulos;
    @FXML private Label lblTotalVenta;
    @FXML private Label lblVendedor;
    @FXML private Label lblCliente;
    @FXML private Label lblClienteId;
    @FXML private Label lblFechaHora;
    @FXML private Label lblCodigo;
    @FXML private Button btnCerrar, btnExportar;
    @FXML private Label lblSubtotal;
    @FXML private Label lblImpuesto;
    @FXML private TableColumn<VentaDetalle, String> colCodigo;
    @FXML private TableColumn<VentaDetalle, String> colProducto;
    @FXML private TableColumn<VentaDetalle, Integer> colCantidad;
    @FXML private TableColumn<VentaDetalle, BigDecimal> colPrecio;
    @FXML private TableColumn<VentaDetalle, BigDecimal> colSubtotal;

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(cellData -> {
            var product = cellData.getValue().getProduct();
            return new SimpleStringProperty(product != null ? product.getCode() : "N/A");
        });
        colProducto.setCellValueFactory(cellData -> {
            var product = cellData.getValue().getProduct();
            return new SimpleStringProperty(product != null ? product.getName() : "Producto Desconocido");
        });
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

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

    public void setVenta(Venta venta) {
        if (venta == null) return;
        lblCodigo.setText(venta.getCode());
        
        String dateStr = venta.getSaleDate();
        if (dateStr != null && dateStr.contains("T")) {
            dateStr = dateStr.replace("T", " ");
            if (dateStr.contains(".")) {
                dateStr = dateStr.substring(0, dateStr.lastIndexOf("."));
            }
        }
        lblFechaHora.setText(dateStr != null ? dateStr : "");
        
        lblVendedor.setText(venta.getUser() != null ? venta.getUser().getUsername() : "N/A");
        lblCliente.setText(venta.getCustomer() != null ? venta.getCustomer().getPerson().getFullName() : "N/A");
        lblClienteId.setText(venta.getCustomer() != null ? venta.getCustomer().getCode() : "N/A");
        List<VentaDetalle> detalles = venta.getDetails();
        tblProductos.setItems(FXCollections.observableArrayList(detalles));
        int totalArticulos = detalles.stream().mapToInt(VentaDetalle::getQuantity).sum();
        lblTotalArticulos.setText(String.valueOf(totalArticulos));
        BigDecimal total = BigDecimal.ZERO;
        for (VentaDetalle detalle : detalles) {
            total = total.add(detalle.getSubtotal());
        }
        BigDecimal subtotal = total.divide(BigDecimal.valueOf(1.18), 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(subtotal);
        lblSubtotal.setText("S/ " + subtotal);
        lblImpuesto.setText("S/ " + igv.setScale(2, RoundingMode.HALF_UP).toString());
        lblTotalVenta.setText("S/ " + total);
    }

}
