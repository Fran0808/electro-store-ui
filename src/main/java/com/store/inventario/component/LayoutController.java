package com.store.inventario.component;

import com.store.inventario.shared.model.NavigationManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LayoutController {
    @FXML
    private StackPane mainContent;

    @FXML
    private void initialize() {
        javafx.application.Platform.runLater(() -> {
            try {
                if (mainContent.getScene() != null && mainContent.getScene().getWindow() instanceof Stage) {
                    Stage stage = (Stage) mainContent.getScene().getWindow();
                    Image icon = new Image(getClass().getResourceAsStream("/images/logo.png"));
                    if (stage.getIcons().isEmpty() || !stage.getIcons().contains(icon)) {
                        stage.getIcons().add(icon);
                    }
                }
            } catch (Exception e) {
                System.err.println("No se pudo cargar el logo de la aplicación: " + e.getMessage());
            }
        });
        NavigationManager.getInstance().setOnNavegar(path -> {
            try {
                cargarVista(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        NavigationManager.getInstance().navegar("/views/component/index.fxml");
    }

    private void cargarVista(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent vista = loader.load();
        mainContent.getChildren().setAll(vista);
    }


}