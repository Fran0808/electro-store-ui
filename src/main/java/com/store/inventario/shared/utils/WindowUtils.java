package com.store.inventario.shared.utils;

import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class WindowUtils {
    private static Image appIcon;

    public static Image getAppIcon() {
        if (appIcon == null) {
            try {
                appIcon = new Image(Objects.requireNonNull(WindowUtils.class.getResourceAsStream("/images/logo.png")));
            } catch (Exception e) {
                System.err.println("No se pudo cargar el logo de la aplicación: " + e.getMessage());
            }
        }
        return appIcon;
    }

    public static void applyIcon(Stage stage) {
        Image icon = getAppIcon();
        if (icon != null) {
            stage.getIcons().add(icon);
        }
    }

    public static void applyIcon(javafx.scene.control.Alert alert) {
        if (alert != null && alert.getDialogPane().getScene().getWindow() instanceof Stage) {
            applyIcon((Stage) alert.getDialogPane().getScene().getWindow());
        }
    }
}
