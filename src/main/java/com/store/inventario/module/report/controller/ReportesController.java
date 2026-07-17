package com.store.inventario.module.report.controller;

import com.store.inventario.module.report.service.ReportService;
import com.store.inventario.shared.utils.WindowUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileOutputStream;
import java.awt.Desktop;
import java.time.LocalDate;

public class ReportesController {

    // Reporte de Ventas bindings
    @FXML private ComboBox<String> cbVentasFrecuencia;
    @FXML private DatePicker dpVentasFechaRef;

    // Reporte de Compras bindings
    @FXML private ComboBox<String> cbComprasFrecuencia;
    @FXML private DatePicker dpComprasFechaRef;

    // Kardex bindings
    @FXML private TextField txtKardexProducto;
    @FXML private DatePicker dpKardexInicio;
    @FXML private DatePicker dpKardexFin;

    private final ReportService reportService = new ReportService();

    @FXML
    public void initialize() {
        // Ventas
        cbVentasFrecuencia.getItems().addAll("Anual", "Mensual", "Semanal");
        cbVentasFrecuencia.getSelectionModel().selectFirst();
        dpVentasFechaRef.setValue(LocalDate.now());

        // Compras
        cbComprasFrecuencia.getItems().addAll("Anual", "Mensual", "Semanal");
        cbComprasFrecuencia.getSelectionModel().selectFirst();
        dpComprasFechaRef.setValue(LocalDate.now());

        // Kardex
        dpKardexInicio.setValue(LocalDate.now().withDayOfYear(1));
        dpKardexFin.setValue(LocalDate.now());
    }

    @FXML
    private void handleGenerarVentas() {
        String frecuencia = cbVentasFrecuencia.getValue();
        LocalDate fechaRef = dpVentasFechaRef.getValue();

        if (frecuencia == null || fechaRef == null) {
            mostrarAlertaWarning("Campos Requeridos", "Debe seleccionar la frecuencia y la fecha de referencia.");
            return;
        }

        String freqParam = "ANNUAL";
        if ("Mensual".equals(frecuencia)) {
            freqParam = "MONTHLY";
        } else if ("Semanal".equals(frecuencia)) {
            freqParam = "WEEKLY";
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte de Ventas");
            fileChooser.setInitialFileName("Reporte_Ventas_" + freqParam + "_" + fechaRef + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf")
            );

            Stage stage = (Stage) cbVentasFrecuencia.getScene().getWindow();
            File destFile = fileChooser.showSaveDialog(stage);

            if (destFile != null) {
                byte[] pdfBytes = reportService.descargarReporteVentas(freqParam, fechaRef.toString());

                try (FileOutputStream fos = new FileOutputStream(destFile)) {
                    fos.write(pdfBytes);
                }

                mostrarAlerta("Éxito", "Reporte de ventas guardado exitosamente en: " + destFile.getAbsolutePath());
                
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(destFile);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "No se pudo generar el reporte de ventas: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerarCompras() {
        String frecuencia = cbComprasFrecuencia.getValue();
        LocalDate fechaRef = dpComprasFechaRef.getValue();

        if (frecuencia == null || fechaRef == null) {
            mostrarAlertaWarning("Campos Requeridos", "Debe seleccionar la frecuencia y la fecha de referencia.");
            return;
        }

        String freqParam = "ANNUAL";
        if ("Mensual".equals(frecuencia)) {
            freqParam = "MONTHLY";
        } else if ("Semanal".equals(frecuencia)) {
            freqParam = "WEEKLY";
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte de Compras");
            fileChooser.setInitialFileName("Reporte_Compras_" + freqParam + "_" + fechaRef + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf")
            );

            Stage stage = (Stage) cbComprasFrecuencia.getScene().getWindow();
            File destFile = fileChooser.showSaveDialog(stage);

            if (destFile != null) {
                byte[] pdfBytes = reportService.descargarReporteCompras(freqParam, fechaRef.toString());

                try (FileOutputStream fos = new FileOutputStream(destFile)) {
                    fos.write(pdfBytes);
                }

                mostrarAlerta("Éxito", "Reporte de compras guardado exitosamente en: " + destFile.getAbsolutePath());
                
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(destFile);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "No se pudo generar el reporte de compras: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerarKardex() {
        String producto = txtKardexProducto.getText();
        LocalDate inicio = dpKardexInicio.getValue();
        LocalDate fin = dpKardexFin.getValue();

        if (inicio == null || fin == null) {
            mostrarAlertaWarning("Validación", "Por favor, seleccione las fechas de inicio y fin para el rango del Kardex.");
            return;
        }

        if (inicio.isAfter(fin)) {
            mostrarAlertaWarning("Rango de Fechas Incorrecto", "La fecha de inicio no puede ser posterior a la fecha de fin.");
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte de Kardex");
            
            String fileName = (producto != null && !producto.trim().isEmpty()) 
                ? "Reporte_Kardex_" + producto.trim() + "_" + inicio + "_a_" + fin + ".pdf" 
                : "Reporte_Kardex_General_" + inicio + "_a_" + fin + ".pdf";
            fileChooser.setInitialFileName(fileName);
            
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf")
            );

            Stage stage = (Stage) txtKardexProducto.getScene().getWindow();
            File destFile = fileChooser.showSaveDialog(stage);

            if (destFile != null) {
                String prodParam = (producto != null && !producto.trim().isEmpty()) ? producto.trim() : null;
                byte[] pdfBytes = reportService.descargarReporteKardex(prodParam, inicio.toString(), fin.toString());

                try (FileOutputStream fos = new FileOutputStream(destFile)) {
                    fos.write(pdfBytes);
                }

                mostrarAlerta("Éxito", "Reporte de Kardex guardado exitosamente en: " + destFile.getAbsolutePath());
                
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(destFile);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "No se pudo generar el reporte de Kardex: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType type, String titulo, String mensaje) {
        Alert alert = new Alert(type);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        WindowUtils.applyIcon(alert);
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    private void mostrarAlertaWarning(String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.WARNING, titulo, mensaje);
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.ERROR, titulo, mensaje);
    }
}