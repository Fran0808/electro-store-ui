package com.store.inventario.shared.utils;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

public class NotificationUtils {

    public static void showToast(Window window, String message) {
        if (window == null) return;

        Popup popup = new Popup();
        
        Label label = new Label(message);
        label.setStyle("-fx-text-fill: white; " +
                       "-fx-font-size: 13px; " +
                       "-fx-font-weight: bold; " +
                       "-fx-font-family: 'Inter', 'Segoe UI', sans-serif; " +
                       "-fx-background-color: #10B981; " +
                       "-fx-background-radius: 6px; " +
                       "-fx-padding: 8px 16px 8px 16px; " +
                       "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 8, 0, 0, 4);");

        StackPane container = new StackPane(label);
        container.setPadding(new Insets(10));
        popup.getContent().add(container);

        popup.setOpacity(0.0);
        popup.show(window);

        double x = window.getX() + ((window.getWidth() - container.getWidth()) / 2.0) + 110;
        double y = window.getY() + window.getHeight() - container.getHeight() - 75;
        popup.setX(x);
        popup.setY(y);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), container);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        PauseTransition hold = new PauseTransition(Duration.millis(1200));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), container);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeIn.setOnFinished(e -> popup.setOpacity(1.0));
        
        SequentialTransition transition = new SequentialTransition(fadeIn, hold, fadeOut);
        transition.setOnFinished(e -> popup.hide());

        popup.setOpacity(1.0);
        transition.play();
    }
}
