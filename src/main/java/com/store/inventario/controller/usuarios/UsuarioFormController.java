package com.store.inventario.controller.usuarios;

import com.store.inventario.model.usuario.Usuario;
import com.store.inventario.service.usuario.UsuarioService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UsuarioFormController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<String> cbRol;
    @FXML
    private ComboBox<String> cbEmpleado;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnGuardar;

    private final UsuarioService usuarioService = new UsuarioService();
    private Usuario usuarioEditar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Cargar Roles del enum del backend
        cbRol.setItems(FXCollections.observableArrayList("ADMIN", "RECEPTION", "STOREKEEPER"));

        // Cargar empleados de muestra para la vista, de ahi se cambiaa
        cbEmpleado.setItems(FXCollections.observableArrayList(
            "EMP001 - Juan Pérez",
            "EMP002 - María Gómez",
            "EMP003 - Carlos Rodríguez"
        ));
    }

    public void setUsuarioEditar(Usuario usuario) {
        this.usuarioEditar = usuario;
        if (lblTitulo != null) {
            lblTitulo.setText("Editar Usuario");
        }
        txtUsername.setText(usuario.getUsername());
        txtUsername.setEditable(false);
        cbRol.setValue(usuario.getRole());
        cbEmpleado.setValue(usuario.getEmployeeCode() + " - " + usuario.getFullName());

        txtPassword.setPromptText("Opcional (Dejar vacío para no cambiar)");
    }

    @FXML
    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void guardarUsuario() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String rol = cbRol.getValue();
        String empleadoSeleccionado = cbEmpleado.getValue();

        boolean esNuevo = (usuarioEditar == null);
        
        if (username == null || username.trim().isEmpty() ||
            (esNuevo && (password == null || password.trim().isEmpty())) ||
            rol == null || empleadoSeleccionado == null) {

            mostrarAlerta("Campos Requeridos", "Por favor, complete todos los campos obligatorios.");
            return;
        }

        try {
            String employeeCode = empleadoSeleccionado.split(" - ")[0];

            Usuario nuevoUsuario = new Usuario(
                esNuevo ? null : usuarioEditar.getCode(),
                username.trim(),
                rol,
                employeeCode,
                "",
                ""
            );

            if (esNuevo) {
                usuarioService.crearUsuario(nuevoUsuario);
            } else {
                usuarioService.actualizarUsuario(usuarioEditar.getCode(), nuevoUsuario);
            }

            cerrarModal();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al Guardar", "Ocurrió un error al intentar guardar el usuario: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
