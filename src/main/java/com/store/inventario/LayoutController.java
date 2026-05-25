package com.store.inventario;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class LayoutController {
    @FXML
    private StackPane mainContent;

    private void cargarVista(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent vista = loader.load();
        mainContent.getChildren().setAll(vista);
    }


}