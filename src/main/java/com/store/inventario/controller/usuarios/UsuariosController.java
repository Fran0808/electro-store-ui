package com.store.inventario.controller.usuarios;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.usuario.Usuario;
import com.store.inventario.service.usuario.UsuarioService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    @FXML
    private TableColumn<Usuario, Void> colAcciones;

    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    public void abrirModalNuevoUsuario() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/usuarios/usuarios-form.fxml"));
        Parent root = loader.load();
        
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Nuevo Usuario");
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

        configurarColumnaAcciones();
        obtenerUsuarios();
    }

    private void configurarColumnaAcciones() {
        Callback<TableColumn<Usuario, Void>, TableCell<Usuario, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Usuario, Void> call(final TableColumn<Usuario, Void> param) {
                return new TableCell<>() {
                    private final Button btnAcciones = new Button("⋮");
                    private final ContextMenu menuAcciones = new ContextMenu();
                    private final MenuItem itemEditar = new MenuItem("Editar");
                    private final MenuItem itemEliminar = new MenuItem("Eliminar");
                    private final HBox contenedor = new HBox(btnAcciones);

                    {
                        btnAcciones.getStyleClass().add("btn-acciones");
                        btnAcciones.setTooltip(new Tooltip("Acciones de Usuario"));

                        itemEditar.getStyleClass().add("menu-item-editar");
                        itemEliminar.getStyleClass().add("menu-item-eliminar");
                        menuAcciones.getItems().addAll(itemEditar, itemEliminar);

                        contenedor.setAlignment(Pos.CENTER);

                        btnAcciones.setOnAction(event -> {
                            menuAcciones.show(btnAcciones, javafx.geometry.Side.BOTTOM, 0, 0);
                        });

                        itemEditar.setOnAction(event -> {
                            Usuario usuario = getTableView().getItems().get(getIndex());
                            handleEditar(usuario);
                        });

                        itemEliminar.setOnAction(event -> {
                            Usuario usuario = getTableView().getItems().get(getIndex());
                            handleEliminar(usuario);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(contenedor);
                        }
                    }
                };
            }
        };
        colAcciones.setCellFactory(cellFactory);
    }

    private void handleEditar(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/usuarios/usuarios-form.fxml"));
            Parent root = loader.load();

            UsuarioFormController controller = loader.getController();
            controller.setUsuarioEditar(usuario);

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Editar Usuario");
            modal.setResizable(false);
            modal.setScene(new Scene(root));
            modal.showAndWait();

            obtenerUsuarios();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEliminar(Usuario usuario) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar usuario?");
        confirmacion.setContentText("Se eliminará permanentemente al usuario \"" + usuario.getUsername() + "\" (Código: " + usuario.getCode() + "). Esta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                usuarioService.eliminarUsuario(usuario.getCode());

                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Éxito");
                exito.setHeaderText("Usuario Eliminado");
                exito.setContentText("El usuario se ha eliminado correctamente del sistema.");
                exito.showAndWait();

                obtenerUsuarios();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void obtenerUsuarios() {
        try {
            PageResponse<Usuario> response = usuarioService.obtenerUsuarios();
            List<Usuario> usuarios = (response != null) ? response.getContent() : java.util.Collections.emptyList();
            tblUsuarios.setItems(FXCollections.observableArrayList(usuarios));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
