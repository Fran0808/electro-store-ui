package com.store.inventario.module.auth.controller;

import com.store.inventario.module.auth.service.LoginService;
import com.store.inventario.security.SessionManager;
import com.store.inventario.shared.utils.DialogUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.shape.SVGPath;
import java.io.IOException;

public class LoginController {

    @FXML private CheckBox chMantenerSesion;
    @FXML private Button btnLogin;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordText;
    @FXML private SVGPath svgEyeIcon;

    private boolean isPasswordVisible = false;

    private static final String EYE_PATH = "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z";
    private static final String EYE_OFF_PATH = "M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.82l2.92 2.92c1.51-1.44 2.63-3.21 3.44-5.24-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C11.74 7.13 11.87 7 12 7zm0 10c-2.76 0-5-2.24-5-5 0-.65.13-1.26.36-1.82l-2.92-2.92c-1.51 1.44-2.63 3.21-3.44 5.24 1.73 4.39 6 7.5 11 7.5 1.4 0 2.74-.25 3.98-.7l-2.16-2.16c-.24.23-.53.36-.82.36zm0-8c1.66 0 3 1.34 3 3 0 .37-.07.72-.18 1.05l-3.87-3.87c.33-.11.68-.18 1.05-.18zm0 6c-1.66 0-3-1.34-3-3 0-.37.07-.72.18-1.05l3.87 3.87c-.33.11-.68.18-1.05.18z M2.78 2.56L1.5 3.84l18.66 18.66 1.28-1.28z";

    private final LoginService loginService = new LoginService();

    @FXML
    private void initialize() {
        btnLogin.setDefaultButton(true);
        if (txtPasswordText != null && txtPassword != null) {
            txtPasswordText.textProperty().bindBidirectional(txtPassword.textProperty());
        }
    }

    @FXML
    private void toggleMostrarPassword() {
        isPasswordVisible = !isPasswordVisible;
        txtPasswordText.setVisible(isPasswordVisible);
        txtPassword.setVisible(!isPasswordVisible);
        if (isPasswordVisible) {
            if (svgEyeIcon != null) svgEyeIcon.setContent(EYE_OFF_PATH);
            txtPasswordText.requestFocus();
            txtPasswordText.selectPositionCaret(txtPasswordText.getLength());
        } else {
            if (svgEyeIcon != null) svgEyeIcon.setContent(EYE_PATH);
            txtPassword.requestFocus();
            txtPassword.selectPositionCaret(txtPassword.getLength());
        }
    }

    @FXML
    private void handleLogging(ActionEvent event) {
        String usuario = txtUsuario.getText() != null ? txtUsuario.getText().trim() : "";
        String password = txtPassword.getText() != null ? txtPassword.getText() : "";

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Campos Requeridos", "Por favor, introduce tu usuario y contraseña.", "ADVERTENCIA", "ADVERTENCIA");
            return;
        }

        try {
            boolean loginExitoso = loginService.autenticar(usuario, password);

            if (loginExitoso) {
                if (chMantenerSesion.isSelected()) {
                    SessionManager.getInstance().guardarSesion(
                            SessionManager.getInstance().getToken(),
                            SessionManager.getInstance().getUsername(),
                            SessionManager.getInstance().getRole(),
                            true
                    );
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/component/layout-view.fxml"));
                Parent root = loader.load();

                Stage layoutStage = new Stage();
                com.store.inventario.shared.utils.WindowUtils.applyIcon(layoutStage);
                layoutStage.setTitle("ElectroStore System");
                layoutStage.setScene(new Scene(root));
                layoutStage.setMinWidth(1280);
                layoutStage.setMinHeight(800);
                layoutStage.setMaximized(true);
                layoutStage.show();

                Stage loginStage = (Stage) btnLogin.getScene().getWindow();
                loginStage.close();
            } else {
                mostrarAlerta("Acceso Denegado", "Usuario o contraseña inválidos.", "ERROR", "ERROR");
            }
        } catch (RuntimeException e) {
            mostrarAlerta("Servidor no disponible", "No se pudo conectar con el backend.", "CONEXIÓN", "ERROR");
        } catch (IOException e) {
            mostrarAlerta("Error de Carga", "No se pudo cargar la vista principal.", "SISTEMA", "ERROR");
        }
    }

    private void mostrarAlerta(String titulo, String contenido, String categoria, String icono) {
        Stage stageActual = (Stage) btnLogin.getScene().getWindow();
        DialogUtils.mostrarMensaje(stageActual, titulo, contenido, categoria, icono, null, false, true);
    }
}