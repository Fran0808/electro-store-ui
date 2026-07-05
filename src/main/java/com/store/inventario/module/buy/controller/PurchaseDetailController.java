package com.store.inventario.module.buy.controller;

import com.store.inventario.module.buy.model.entity.Purchase;
import com.store.inventario.module.buy.model.entity.PurchaseDetail;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

public class PurchaseDetailController {

    @FXML private Label lblCodigoCompra;
    @FXML private Label lblFecha;
    @FXML private Label lblProveedor;
    @FXML private Label lblResponsable;
    @FXML private Label lblItemsCount;

    @FXML private TableView<PurchaseDetail> tblDetalleCompra;
    @FXML private TableColumn<PurchaseDetail, String> colProducto;
    @FXML private TableColumn<PurchaseDetail, Integer> colCantidad;
    @FXML private TableColumn<PurchaseDetail, BigDecimal> colPrecio;
    @FXML private TableColumn<PurchaseDetail, BigDecimal> colSubtotal;

    @FXML private Label lblSubtotal;
    @FXML private Label lblImpuesto;
    @FXML private Label lblTotal;
    @FXML private Button btnCerrar;

    @FXML
    public void initialize() {
        colProducto.setCellValueFactory(cellData -> {
            var product = cellData.getValue().getProduct();
            return new SimpleStringProperty(product != null ? product.getName() : "Producto Desconocido");
        });
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    public void setCompra(Purchase purchase) {
        if (purchase == null) return;

        lblCodigoCompra.setText(purchase.getCode() != null ? purchase.getCode() : "N/A");
        
        String dateStr = purchase.getPurchaseDate();
        if (dateStr != null && dateStr.contains("T")) {
            dateStr = dateStr.replace("T", " ");
            if (dateStr.contains(".")) {
                dateStr = dateStr.substring(0, dateStr.lastIndexOf("."));
            }
        }
        lblFecha.setText(dateStr != null ? dateStr : "N/A");
        
        lblProveedor.setText(purchase.getSupplier() != null ? purchase.getSupplier().getTradeName() : "N/A");
        lblResponsable.setText(purchase.getUser() != null ? purchase.getUser().getUsername() : "N/A");

        List<PurchaseDetail> details = purchase.getDetails() != null ? purchase.getDetails() : Collections.emptyList();
        tblDetalleCompra.setItems(FXCollections.observableArrayList(details));
        lblItemsCount.setText("Items: " + details.size());

        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseDetail detail : details) {
            BigDecimal price = detail.getPurchasePrice() != null ? detail.getPurchasePrice() : BigDecimal.ZERO;
            total = total.add(price.multiply(BigDecimal.valueOf(detail.getQuantity())));
        }

        BigDecimal factor = BigDecimal.valueOf(1.18);
        BigDecimal subtotal = total.divide(factor, 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(subtotal);

        lblSubtotal.setText("S/ " + subtotal.setScale(2, RoundingMode.HALF_UP).toString());
        lblImpuesto.setText("S/ " + igv.setScale(2, RoundingMode.HALF_UP).toString());
        lblTotal.setText("S/ " + total.setScale(2, RoundingMode.HALF_UP).toString());
    }

    @FXML
    private void cerrarModal() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}
