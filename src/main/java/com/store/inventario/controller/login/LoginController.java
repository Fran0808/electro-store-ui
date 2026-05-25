package com.store.inventario.controller.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private Button btnLogin;
    @FXML
    private void initialize() {
    }

    @FXML
    private void handleLogging(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/layout-view.fxml"));
        Parent root = loader.load();

        Stage layoutStage = new Stage();
        layoutStage.setTitle("ElectroStore");
        layoutStage.setScene(new Scene(root));
        layoutStage.show();

        Stage loginStage = (Stage) btnLogin.getScene().getWindow();
        loginStage.close();
    }
}