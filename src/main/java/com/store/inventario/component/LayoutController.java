package com.store.inventario.component;

import com.store.inventario.shared.model.NavigationManager;
import com.store.inventario.module.backup.BackupService;
import com.store.inventario.security.SessionManager;
import com.store.inventario.shared.utils.WindowUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class LayoutController {
    @FXML
    private StackPane mainContent;

    private Timeline inactivityTimeline;

    @FXML
    private void initialize() {
        javafx.application.Platform.runLater(() -> {
            try {
                if (mainContent.getScene() != null && mainContent.getScene().getWindow() instanceof Stage) {
                    Stage stage = (Stage) mainContent.getScene().getWindow();
                    Image icon = new Image(getClass().getResourceAsStream("/images/logo.png"));
                    if (stage.getIcons().isEmpty() || !stage.getIcons().contains(icon)) {
                        stage.getIcons().add(icon);
                    }
                    configurarCierreAutomatico(stage);
                    iniciarMonitorInactividad(mainContent.getScene(), stage);
                }
            } catch (Exception e) {
                System.err.println("No se pudo cargar el logo de la aplicación: " + e.getMessage());
            }
        });
        NavigationManager.getInstance().setOnNavegar(path -> {
            try {
                cargarVista(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        NavigationManager.getInstance().navegar("/views/component/index.fxml");
    }

    private void cargarVista(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent vista = loader.load();
        mainContent.getChildren().setAll(vista);
    }

    private void configurarCierreAutomatico(Stage stage) {
        stage.setOnCloseRequest(event -> {
            try {
                Preferences prefs = Preferences.userNodeForPackage(AjustesController.class);
                if (prefs.getBoolean("respaldos_auto", false)) {
                    String defaultBackupPath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "ElectroStore" + File.separator + "Backups";
                    String rutaRespaldos = prefs.get("ruta_respaldos", defaultBackupPath);
                    File dir = new File(rutaRespaldos);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    BackupService backupService = new BackupService();
                    backupService.descargarBackup(dir);
                    System.out.println("Backup automático generado con éxito al cerrar.");
                }
            } catch (Exception e) {
                System.err.println("Error al generar backup automático al cerrar: " + e.getMessage());
            }
        });
    }

    private void iniciarMonitorInactividad(Scene scene, Stage stage) {
        Preferences prefs = Preferences.userNodeForPackage(AjustesController.class);
        String expiracion = prefs.get("expiracion_sesion", "30 min");

        if ("Nunca".equalsIgnoreCase(expiracion.trim())) {
            return;
        }

        int minutos = 30;
        try {
            minutos = Integer.parseInt(expiracion.split(" ")[0].trim());
        } catch (Exception e) {
            minutos = 30;
        }
        double totalSegundos = minutos * 60;

        inactivityTimeline = new Timeline(new KeyFrame(Duration.seconds(totalSegundos), event -> {
            cerrarSesionPorInactividad(stage);
        }));
        inactivityTimeline.setCycleCount(1);
        inactivityTimeline.play();

        scene.addEventFilter(MouseEvent.ANY, e -> reiniciarTemporizador());
        scene.addEventFilter(KeyEvent.ANY, e -> reiniciarTemporizador());
    }

    private void reiniciarTemporizador() {
        if (inactivityTimeline != null) {
            inactivityTimeline.playFromStart();
        }
    }

    private void cerrarSesionPorInactividad(Stage currentStage) {
        Platform.runLater(() -> {
            try {
                if (inactivityTimeline != null) {
                    inactivityTimeline.stop();
                }

                SessionManager.getInstance().close();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/auth/login.fxml"));
                Parent root = loader.load();

                Stage loginStage = new Stage();
                WindowUtils.applyIcon(loginStage);
                loginStage.setTitle("Sistema de Inventario");
                loginStage.setScene(new Scene(root));
                loginStage.setMinWidth(1280);
                loginStage.setMinHeight(800);
                loginStage.setMaximized(true);
                loginStage.show();

                if (currentStage != null) {
                    currentStage.close();
                }

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Sesión Expirada");
                alert.setHeaderText("Inactividad Detectada");
                alert.setContentText("Su sesión ha sido cerrada debido a inactividad.");
                WindowUtils.applyIcon(alert);
                alert.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}