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

    @FXML private ComboBox<Integer> cbAnio;

    @FXML private ComboBox<String> cbComprasFrecuencia;
    @FXML private DatePicker dpComprasFechaRef;

    @FXML private TextField txtKardexProducto;
    @FXML private DatePicker dpKardexInicio;
    @FXML private DatePicker dpKardexFin;

    private final ReportService reportService = new ReportService();

    @FXML
    public void initialize() {
        // Ventas
        int anioActual = LocalDate.now().getYear();
        for (int i = anioActual; i >= 2020; i--) {
            cbAnio.getItems().add(i);
        }
        cbAnio.getSelectionModel().selectFirst();

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
        Integer anio = cbAnio.getValue();
        if (anio == null) return;

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte de Ventas");
            fileChooser.setInitialFileName("Reporte_Ventas_" + anio + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf")
            );

            Stage stage = (Stage) cbAnio.getScene().getWindow();
            File destFile = fileChooser.showSaveDialog(stage);

            if (destFile != null) {
                byte[] pdfBytes = reportService.descargarReporteVentas(anio);

                try (FileOutputStream fos = new FileOutputStream(destFile)) {
                    fos.write(pdfBytes);
                }

                mostrarAlerta("Éxito", "Reporte guardado exitosamente en: " + destFile.getAbsolutePath());
                
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(destFile);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaError("Error", "No se pudo generar el reporte: " + e.getMessage());
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

        if (producto == null || producto.trim().isEmpty()) {
            mostrarAlertaWarning("Validación", "Por favor, ingrese el código del producto (SKU) para generar el Kardex.");
            return;
        }

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
            fileChooser.setInitialFileName("Reporte_Kardex_" + producto.trim() + "_" + inicio + "_a_" + fin + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf")
            );

            Stage stage = (Stage) txtKardexProducto.getScene().getWindow();
            File destFile = fileChooser.showSaveDialog(stage);

            if (destFile != null) {
                byte[] pdfBytes = reportService.descargarReporteKardex(producto.trim(), inicio.toString(), fin.toString());

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