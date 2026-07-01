package com.store.inventario.controller.compra;

import com.store.inventario.model.compra.Compra;
import com.store.inventario.model.compra.CompraDetalle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
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

public class CompraDetailController {

    @FXML private Label lblCodigoCompra;
    @FXML private Label lblFecha;
    @FXML private Label lblProveedor;
    @FXML private Label lblResponsable;
    @FXML private Label lblItemsCount;

    @FXML private TableView<CompraDetalle> tblDetalleCompra;
    @FXML private TableColumn<CompraDetalle, String> colProducto;
    @FXML private TableColumn<CompraDetalle, Integer> colCantidad;
    @FXML private TableColumn<CompraDetalle, BigDecimal> colPrecio;
    @FXML private TableColumn<CompraDetalle, BigDecimal> colSubtotal;

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

    public void setCompra(Compra compra) {
        if (compra == null) return;

        lblCodigoCompra.setText(compra.getCode() != null ? compra.getCode() : "N/A");
        
        String dateStr = compra.getPurchaseDate();
        if (dateStr != null && dateStr.contains("T")) {
            dateStr = dateStr.replace("T", " ");
            if (dateStr.contains(".")) {
                dateStr = dateStr.substring(0, dateStr.lastIndexOf("."));
            }
        }
        lblFecha.setText(dateStr != null ? dateStr : "N/A");
        
        lblProveedor.setText(compra.getSupplier() != null ? compra.getSupplier().getTradeName() : "N/A");
        lblResponsable.setText(compra.getUser() != null ? compra.getUser().getUsername() : "N/A");

        List<CompraDetalle> details = compra.getDetails() != null ? compra.getDetails() : Collections.emptyList();
        tblDetalleCompra.setItems(FXCollections.observableArrayList(details));
        lblItemsCount.setText("Items: " + details.size());

        BigDecimal total = BigDecimal.ZERO;
        for (CompraDetalle detail : details) {
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
