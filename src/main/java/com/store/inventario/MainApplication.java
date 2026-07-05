package com.store.inventario;

import com.store.inventario.security.SessionManager;
import com.store.inventario.shared.utils.WindowUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        String viewPath;
        String title;

        if (SessionManager.getInstance().isAutenticado()) {
            viewPath = "layout-view.fxml";
            title = "ElectroStore System";
        } else {
            viewPath = "views/auth/login.fxml";
            title = "Sistema de Inventario";
        }
        FXMLLoader loader = new FXMLLoader(
                MainApplication.class.getResource(viewPath)
        );
        Scene scene = new Scene(loader.load());
        stage.setTitle(title);

        WindowUtils.applyIcon(stage);

        stage.setScene(scene);
        stage.setMinWidth(1280);
        stage.setMinHeight(800);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}