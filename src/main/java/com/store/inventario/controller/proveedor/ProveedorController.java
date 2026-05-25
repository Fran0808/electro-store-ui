package com.store.inventario.controller.proveedor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class ProveedorController {

    @FXML
    public void abrirModalNuevoProveedor() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/proveedores/proveedor-form.fxml"));
        Parent root = loader.load();
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Nuevo Proveedor");
        modal.setResizable(false);
        modal.setScene(new Scene(root));
        modal.showAndWait();
    }

}

