package com.store.inventario.controller.inventario.movimientos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MovimientosController {

    @FXML
    private void handleForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/inventario/movimientos/movimiento-form.fxml"));
        Parent root = loader.load();
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Registrar Movimiento");
        modal.setScene(new Scene(root));
        modal.showAndWait();
    }
}