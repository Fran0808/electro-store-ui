package com.store.inventario.component;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class AjustesController implements Initializable {

    @FXML private ComboBox<String> cbMoneda;
    @FXML private TextField txtTasaImpuesto;
    @FXML private CheckBox chkAutoGenerarPdf;
    @FXML private TextField txtRutaReportes;
    @FXML private CheckBox chkRespaldosAuto;
    @FXML private TextField txtRutaRespaldos;
    @FXML private ComboBox<String> cbExpiracionSesion;

    private final Preferences prefs = Preferences.userNodeForPackage(AjustesController.class);

    private static final String DEFAULT_MONEDA = "Soles (S/)";
    private static final String DEFAULT_TASA = "18.0";
    private static final boolean DEFAULT_AUTO_PDF = true;
    private static final String DEFAULT_RUTA_REPORTES = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "ElectroStore" + File.separator + "Reportes";
    private static final boolean DEFAULT_BACKUP_AUTO = false;
    private static final String DEFAULT_RUTA_BACKUP = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "ElectroStore" + File.separator + "Backups";
    private static final String DEFAULT_EXPIRACION = "30 min";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbMoneda.setItems(FXCollections.observableArrayList("Soles (S/)", "Dólares ($)", "Euros (€)"));
        cbExpiracionSesion.setItems(FXCollections.observableArrayList("5 min", "15 min", "30 min", "Nunca"));

        cargarPreferencias();
    }

    private void cargarPreferencias() {
        cbMoneda.setValue(prefs.get("moneda", DEFAULT_MONEDA));
        txtTasaImpuesto.setText(prefs.get("tasa_impuesto", DEFAULT_TASA));
        chkAutoGenerarPdf.setSelected(prefs.getBoolean("auto_generar_pdf", DEFAULT_AUTO_PDF));
        txtRutaReportes.setText(prefs.get("ruta_reportes", DEFAULT_RUTA_REPORTES));
        chkRespaldosAuto.setSelected(prefs.getBoolean("respaldos_auto", DEFAULT_BACKUP_AUTO));
        txtRutaRespaldos.setText(prefs.get("ruta_respaldos", DEFAULT_RUTA_BACKUP));
        cbExpiracionSesion.setValue(prefs.get("expiracion_sesion", DEFAULT_EXPIRACION));
    }

    @FXML
    private void handleExaminarReportes() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Seleccionar Carpeta para Reportes");

        File initialDir = new File(txtRutaReportes.getText());
        if (initialDir.exists() && initialDir.isDirectory()) {
            chooser.setInitialDirectory(initialDir);
        } else {
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }

        File selectedDir = chooser.showDialog(txtRutaReportes.getScene().getWindow());
        if (selectedDir != null) {
            txtRutaReportes.setText(selectedDir.getAbsolutePath());
        }
    }

    @FXML
    private void handleExaminarRespaldos() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Seleccionar Carpeta para Copias de Seguridad");
        
        File initialDir = new File(txtRutaRespaldos.getText());
        if (initialDir.exists() && initialDir.isDirectory()) {
            chooser.setInitialDirectory(initialDir);
        } else {
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }

        File selectedDir = chooser.showDialog(txtRutaRespaldos.getScene().getWindow());
        if (selectedDir != null) {
            txtRutaRespaldos.setText(selectedDir.getAbsolutePath());
        }
    }

    @FXML
    private void handleGuardarCambios() {
        String tasaStr = txtTasaImpuesto.getText().trim();
        try {
            double tasa = Double.parseDouble(tasaStr);
            if (tasa < 0 || tasa > 100) {
                mostrarAlerta(Alert.AlertType.WARNING, "Validación", "La tasa de impuesto debe estar entre 0% y 100%.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "La tasa de impuesto debe ser un número decimal válido.");
            return;
        }

        File dirReportes = new File(txtRutaReportes.getText().trim());
        if (!dirReportes.exists()) {
            if (!dirReportes.mkdirs()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Validación", "La ruta de reportes no existe y no se pudo crear.");
                return;
            }
        }

        File dirRespaldos = new File(txtRutaRespaldos.getText().trim());
        if (!dirRespaldos.exists()) {
            if (!dirRespaldos.mkdirs()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Validación", "La ruta de respaldos no existe y no se pudo crear.");
                return;
            }
        }

        prefs.put("moneda", cbMoneda.getValue());
        prefs.put("tasa_impuesto", tasaStr);
        prefs.putBoolean("auto_generar_pdf", chkAutoGenerarPdf.isSelected());
        prefs.put("ruta_reportes", txtRutaReportes.getText().trim());
        prefs.putBoolean("respaldos_auto", chkRespaldosAuto.isSelected());
        prefs.put("ruta_respaldos", txtRutaRespaldos.getText().trim());
        prefs.put("expiracion_sesion", cbExpiracionSesion.getValue());

        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Configuración guardada correctamente.");
    }

    @FXML
    private void handleRestablecer() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Restablecer Configuración");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Está seguro de que desea restablecer la configuración a los valores por defecto?");
        com.store.inventario.shared.utils.WindowUtils.applyIcon(confirm);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cbMoneda.setValue(DEFAULT_MONEDA);
                txtTasaImpuesto.setText(DEFAULT_TASA);
                chkAutoGenerarPdf.setSelected(DEFAULT_AUTO_PDF);
                txtRutaReportes.setText(DEFAULT_RUTA_REPORTES);
                chkRespaldosAuto.setSelected(DEFAULT_BACKUP_AUTO);
                txtRutaRespaldos.setText(DEFAULT_RUTA_BACKUP);
                cbExpiracionSesion.setValue(DEFAULT_EXPIRACION);
            }
        });
    }

    private void mostrarAlerta(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        com.store.inventario.shared.utils.WindowUtils.applyIcon(alert);
        alert.showAndWait();
    }
}
