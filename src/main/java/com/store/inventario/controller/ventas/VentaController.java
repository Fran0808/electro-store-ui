package com.store.inventario.controller.ventas;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class VentaController {
    @FXML
    private ComboBox<String> cbTipoComprobante;

    @FXML
    private ComboBox<String> cbVendedor;

    @FXML
    private ComboBox<String> cbRangoFecha;

    @FXML
    public void initialize() {
        cbTipoComprobante.getItems().addAll("Boleta", "Factura", "Ticket");
        cbVendedor.getItems().addAll("Vendedor 1", "Vendedor 2", "Vendedor 3");
        cbRangoFecha.getItems().addAll("Hoy", "Últimos 7 días", "Últimos 30 días");
    }

    @FXML
    private void handleForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/ventas/ventas-form.fxml"));
        Parent root = loader.load();

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Nueva venta");
        modal.setScene(new Scene(root));
        modal.showAndWait();
    }
}
