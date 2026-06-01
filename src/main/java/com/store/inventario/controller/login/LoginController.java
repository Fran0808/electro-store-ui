package com.store.inventario.controller.login;

import com.store.inventario.service.auth.LoginService;
import com.store.inventario.utils.DialogUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML private Button btnLogin;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    private final LoginService loginService = new LoginService();

    @FXML
    private void initialize() {
        btnLogin.setDefaultButton(true);
    }

    @FXML
    private void handleLogging(ActionEvent event) {
        String usuario = txtUsuario.getText() != null ? txtUsuario.getText().trim() : "";
        String password = txtPassword.getText() != null ? txtPassword.getText().trim() : "";

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Campos Requeridos", "Por favor, introduce tu usuario y contraseña.", "ADVERTENCIA", "ADVERTENCIA");
            return;
        }

        try {
            boolean loginExitoso = loginService.autenticar(usuario, password);

            if (loginExitoso) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/layout-view.fxml"));
                Parent root = loader.load();

                Stage layoutStage = new Stage();
                layoutStage.setTitle("ElectroStore System");
                layoutStage.setScene(new Scene(root));
                layoutStage.setMinWidth(1280);
                layoutStage.setMinHeight(800);
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