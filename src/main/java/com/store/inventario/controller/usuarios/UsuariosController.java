package com.store.inventario.controller.usuarios;

import com.store.inventario.model.PageResponse;

import com.store.inventario.model.usuario.Usuario;
import com.store.inventario.service.usuario.UsuarioService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class UsuariosController {
    @FXML
    private TableView<Usuario> tblUsuarios;
    @FXML
    private TableColumn<Usuario, String> colCodigo;
    @FXML
    private TableColumn<Usuario, String> colUsuario;
    @FXML
    private TableColumn<Usuario, String> colRol;
    @FXML
    private TableColumn<Usuario, String> colCodigoEmpleado;
    @FXML
    private TableColumn<Usuario, String> colNombre;
    @FXML
    private TableColumn<Usuario, String> colApellido;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    public void abrirModalNuevoUsuario() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/usuarios/usuarios-form.fxml"));
        Parent root = loader.load();
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Nuevo Empleado");
        modal.setResizable(false);
        modal.setScene(new Scene(root));
        modal.showAndWait();
        obtenerUsuarios();
    }
    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("code"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("role"));
        colCodigoEmpleado.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        obtenerUsuarios();
    }

    private void obtenerUsuarios(){
        try {
            PageResponse<Usuario> response = usuarioService.obtenerUsuarios();
            List<Usuario> usuarios = response.getContent();
            tblUsuarios.setItems(FXCollections.observableArrayList(usuarios));

            //actualizarMetricas(response, productos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
