package com.store.inventario.module.report.controller;

import com.store.inventario.module.report.service.ReportService;
import com.store.inventario.shared.utils.WindowUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    @FXML private ComboBox<String> cbKardexFrecuencia;
    @FXML private HBox containerKardexRango;
    @FXML private VBox containerKardexFechaRef;
    @FXML private DatePicker dpKardexInicio;
    @FXML private DatePicker dpKardexFin;
    @FXML private DatePicker dpKardexFechaRef;

    private final ReportService reportService = new ReportService();

    @FXML
    public void initialize() {
        // Ventas:
        cbVentasFrecuencia.getItems().addAll("Anual", "Mensual", "Semanal", "Diario");
        cbVentasFrecuencia.getSelectionModel().selectFirst();
        dpVentasFechaRef.setValue(LocalDate.now());

        // Compras:
        cbComprasFrecuencia.getItems().addAll("Anual", "Mensual", "Semanal", "Diario");
        cbComprasFrecuencia.getSelectionModel().selectFirst();
        dpComprasFechaRef.setValue(LocalDate.now());

        // Kardex Frecuencias:
        cbKardexFrecuencia.getItems().addAll("Rango Personalizado", "Anual", "Mensual", "Semanal", "Diario");
        cbKardexFrecuencia.getSelectionModel().selectFirst();

        // Kardex Dates:
        dpKardexInicio.setValue(LocalDate.now().withDayOfYear(1));
        dpKardexFin.setValue(LocalDate.now());
        dpKardexFechaRef.setValue(LocalDate.now());

        cbKardexFrecuencia.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCustom = "Rango Personalizado".equals(newVal);
            containerKardexRango.setVisible(isCustom);
            containerKardexRango.setManaged(isCustom);
            containerKardexFechaRef.setVisible(!isCustom);
            containerKardexFechaRef.setManaged(!isCustom);
        });

        boolean isCustom = "Rango Personalizado".equals(cbKardexFrecuencia.getValue());
        containerKardexRango.setVisible(isCustom);
        containerKardexRango.setManaged(isCustom);
        containerKardexFechaRef.setVisible(!isCustom);
        containerKardexFechaRef.setManaged(!isCustom);
    }

    @FXML
    private void handleGenerarVentas() {
        String frecuencia = cbVentasFrecuencia.getValue();
        LocalDate fechaRef = dpVentasFechaRef.getValue();

        if (frecuencia == null || fechaRef == null) {
            mostrarAlertaWarning("Campos Requeridos", "Debe seleccionar la frecuencia y la fecha por consultar.");
            return;
        }

        String freqParam = mapFrecuenciaParam(frecuencia);

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
            mostrarAlertaWarning("Campos Requeridos", "Debe seleccionar la frecuencia y la fecha por consultar.");
            return;
        }

        String freqParam = mapFrecuenciaParam(frecuencia);

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
        String frecuencia = cbKardexFrecuencia.getValue();
        boolean isCustom = "Rango Personalizado".equals(frecuencia);

        String startStr = null;
        String endStr = null;
        String freqParam = null;
        String refDateStr = null;
        String fileNamePeriodStr = "";

        if (isCustom) {
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

            startStr = inicio.toString();
            endStr = fin.toString();
            fileNamePeriodStr = inicio + "_a_" + fin;
        } else {
            LocalDate fechaRef = dpKardexFechaRef.getValue();
            if (fechaRef == null) {
                mostrarAlertaWarning("Validación", "Por favor, seleccione la fecha por consultar para el Kardex.");
                return;
            }

            freqParam = mapFrecuenciaParam(frecuencia);
            refDateStr = fechaRef.toString();
            fileNamePeriodStr = freqParam + "_" + fechaRef;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte de Kardex");
            
            String prodClean = (producto != null && !producto.trim().isEmpty()) ? producto.trim() : "General";
            String fileName = "Reporte_Kardex_" + prodClean + "_" + fileNamePeriodStr + ".pdf";
            fileChooser.setInitialFileName(fileName);
            
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf")
            );

            Stage stage = (Stage) txtKardexProducto.getScene().getWindow();
            File destFile = fileChooser.showSaveDialog(stage);

            if (destFile != null) {
                String prodParam = "General".equals(prodClean) ? null : prodClean;
                byte[] pdfBytes = reportService.descargarReporteKardex(prodParam, startStr, endStr, freqParam, refDateStr);

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

    private String mapFrecuenciaParam(String frecuencia) {
        if ("Mensual".equals(frecuencia)) return "MONTHLY";
        if ("Semanal".equals(frecuencia)) return "WEEKLY";
        if ("Diario".equals(frecuencia)) return "DAILY";
        return "ANNUAL"; // Anual por defecto
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