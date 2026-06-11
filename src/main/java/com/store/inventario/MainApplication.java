package com.store.inventario;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MainApplication.class.getResource("views/login.fxml")
        );
        Scene scene = new Scene(loader.load());
        stage.setTitle("Sistema de Inventario");

        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/logo.png")));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el logo de la aplicación: " + e.getMessage());
        }

        stage.setScene(scene);
        stage.setMinWidth(1280);
        stage.setMinHeight(800);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}