package com.store.inventario.controller.login;

import com.store.inventario.service.auth.LoginService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
    }

    @FXML
    private void handleLogging(ActionEvent event) {
        String usuario = txtUsuario.getText() != null ? txtUsuario.getText().trim() : "";
        String password = txtPassword.getText() != null ? txtPassword.getText().trim() : "";

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Campos Requeridos", "Por favor, introduce tu usuario y contraseña.", Alert.AlertType.WARNING);
            return;
        }

        boolean loginExitoso = loginService.autenticar(usuario, password);

        if (loginExitoso) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/layout-view.fxml"));
                Parent root = loader.load();

                Stage layoutStage = new Stage();
                layoutStage.setTitle("ElectroStore System");
                layoutStage.setScene(new Scene(root));
                layoutStage.show();

                Stage loginStage = (Stage) btnLogin.getScene().getWindow();
                loginStage.close();

            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Error de Carga", "No se pudo cargar la vista principal.", Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Acceso Denegado", "Usuario o contraseña inválidos. Verifica tus credenciales.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}