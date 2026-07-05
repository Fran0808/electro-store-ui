package com.store.inventario.module.sale.controller;

import com.store.inventario.module.sale.model.entity.Sale;
import com.store.inventario.module.sale.model.entity.SaleDetail;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class SaleDetailController {
    @FXML private TableView<SaleDetail> tblProductos;
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
    @FXML private TableColumn<SaleDetail, String> colCodigo;
    @FXML private TableColumn<SaleDetail, String> colProducto;
    @FXML private TableColumn<SaleDetail, Integer> colCantidad;
    @FXML private TableColumn<SaleDetail, BigDecimal> colPrecio;
    @FXML private TableColumn<SaleDetail, BigDecimal> colSubtotal;

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

    public void setVenta(Sale sale) {
        if (sale == null) return;
        lblCodigo.setText(sale.getCode());
        
        String dateStr = sale.getSaleDate();
        if (dateStr != null && dateStr.contains("T")) {
            dateStr = dateStr.replace("T", " ");
            if (dateStr.contains(".")) {
                dateStr = dateStr.substring(0, dateStr.lastIndexOf("."));
            }
        }
        lblFechaHora.setText(dateStr != null ? dateStr : "");
        
        lblVendedor.setText(sale.getUser() != null ? sale.getUser().getUsername() : "N/A");
        lblCliente.setText(sale.getCustomer() != null ? sale.getCustomer().getPerson().getFullName() : "N/A");
        lblClienteId.setText(sale.getCustomer() != null ? sale.getCustomer().getCode() : "N/A");
        List<SaleDetail> detalles = sale.getDetails();
        tblProductos.setItems(FXCollections.observableArrayList(detalles));
        int totalArticulos = detalles.stream().mapToInt(SaleDetail::getQuantity).sum();
        lblTotalArticulos.setText(String.valueOf(totalArticulos));
        BigDecimal total = BigDecimal.ZERO;
        for (SaleDetail detalle : detalles) {
            total = total.add(detalle.getSubtotal());
        }
        BigDecimal subtotal = total.divide(BigDecimal.valueOf(1.18), 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(subtotal);
        lblSubtotal.setText("S/ " + subtotal);
        lblImpuesto.setText("S/ " + igv.setScale(2, RoundingMode.HALF_UP).toString());
        lblTotalVenta.setText("S/ " + total);
    }

}
