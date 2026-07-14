package com.store.inventario.module.report.controller;

import com.store.inventario.module.report.service.ReportService;
import com.store.inventario.shared.utils.WindowUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileOutputStream;
import java.awt.Desktop;

public class ReportesController {

    @FXML private ComboBox<Integer> cbAnio;
    private final ReportService reportService = new ReportService();

    @FXML
    public void initialize() {
        int anioActual = java.time.LocalDate.now().getYear();
        for (int i = anioActual; i >= 2020; i--) {
            cbAnio.getItems().add(i);
        }
        cbAnio.getSelectionModel().selectFirst();
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
            mostrarAlerta("Error", "No se pudo generar el reporte: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        WindowUtils.applyIcon(alert);
        alert.showAndWait();
    }
}