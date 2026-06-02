package com.store.inventario.utils;

import javafx.scene.control.TextField;

public class ValidationUtils {

    public static void hacerSoloNumerico(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public static void hacerSoloNumericoConLimite(TextField textField, int limite) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            String sanitized = newValue.replaceAll("[^\\d]", "");
            if (sanitized.length() > limite) {
                sanitized = sanitized.substring(0, limite);
            }
            if (!newValue.equals(sanitized)) {
                textField.setText(sanitized);
            }
        });
    }

    public static void hacerSoloDecimal(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            if (newValue.isEmpty()) return;
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                textField.setText(oldValue != null ? oldValue : "");
            }
        });
    }

    public static void hacerSoloDecimal(TextField textField, int limite) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            if (newValue.isEmpty()) return;
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                textField.setText(oldValue != null ? oldValue : "");
                return;
            }
            if (newValue.length() > limite) {
                textField.setText(oldValue != null ? oldValue : "");
            }
        });
    }

    public static void hacerSoloDecimal(TextField textField, int enteros, int decimales) {
        String regex = "^\\d{0," + enteros + "}(\\.\\d{0," + decimales + "})?$";
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            if (newValue.isEmpty()) return;
            if (!newValue.matches(regex)) {
                textField.setText(oldValue != null ? oldValue : "");
            }
        });
    }

    public static void hacerSoloTelefono(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            if (newValue.isEmpty()) return;
            if (!newValue.matches("\\+?[\\d\\s]*")) {
                textField.setText(oldValue != null ? oldValue : "");
            }
        });
    }
}
