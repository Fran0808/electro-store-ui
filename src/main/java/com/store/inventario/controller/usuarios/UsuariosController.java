package com.store.inventario.controller.usuarios;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.usuario.Usuario;
import com.store.inventario.service.usuario.UsuarioService;
import javafx.application.Platform;
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
    
    @FXML
    private Label lblTotalUsuarios;
    @FXML
    private Label lblTotalAdmins;
    @FXML
    private Label lblTotalOperativos;
    @FXML
    private Label lblResumenPaginacion;

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
        colRol.setCellValueFactory(cellData -> {
            String rolIngles = cellData.getValue().getRole();
            String rolEspanol = "";
            if (rolIngles != null) {
                switch (rolIngles.toUpperCase().trim()) {
                    case "ADMIN": rolEspanol = "ADMIN"; break;
                    case "RECEPTION": rolEspanol = "RECEPCIONISTA"; break;
                    case "STOREKEEPER": rolEspanol = "ALMACENERO"; break;
                    default: rolEspanol = rolIngles.toUpperCase().trim();
                }
            }
            return new javafx.beans.property.SimpleStringProperty(rolEspanol);
        });
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
        Platform.runLater(() -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Eliminacion");
            confirmacion.setHeaderText("Eliminar usuario");
            confirmacion.setContentText("Se eliminara permanentemente al usuario " + usuario.getUsername() + " (Codigo: " + usuario.getCode() + "). Esta accion no se puede deshacer.");

            Optional<ButtonType> resultado = confirmacion.showAndWait();

            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    usuarioService.eliminarUsuario(usuario.getCode());

                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Exito");
                    exito.setHeaderText("Usuario Eliminado");
                    exito.setContentText("El usuario se ha eliminado correctamente del sistema.");
                    exito.showAndWait();

                    obtenerUsuarios();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("No se pudo eliminar el usuario");
                    error.setContentText("No se pudo eliminar al usuario: " + e.getMessage());
                    error.showAndWait();
                }
            }
        });
    }

    private void obtenerUsuarios() {
        try {
            PageResponse<Usuario> response = usuarioService.obtenerUsuarios();
            List<Usuario> usuarios = (response != null) ? response.getContent() : java.util.Collections.emptyList();
            tblUsuarios.setItems(FXCollections.observableArrayList(usuarios));
            
            if (lblTotalUsuarios != null) {
                lblTotalUsuarios.setText(String.valueOf(response != null ? response.getTotalElements() : usuarios.size()));
            }
            
            if (lblTotalAdmins != null) {
                long totalAdmins = usuarios.stream()
                        .filter(u -> u.getRole() != null && "ADMIN".equalsIgnoreCase(u.getRole().trim()))
                        .count();
                lblTotalAdmins.setText(String.valueOf(totalAdmins));
            }
            
            if (lblTotalOperativos != null) {
                long totalOperativos = usuarios.stream()
                        .filter(u -> u.getRole() != null && !"ADMIN".equalsIgnoreCase(u.getRole().trim()))
                        .count();
                lblTotalOperativos.setText(String.valueOf(totalOperativos));
            }

            if (lblResumenPaginacion != null && response != null) {
                long total = response.getTotalElements();
                int paginaActual = response.getNumber();
                int pageSize = response.getSize();
                
                if (total == 0) {
                    lblResumenPaginacion.setText("No hay usuarios para mostrar");
                } else {
                    long desde = (long) paginaActual * pageSize + 1;
                    long hasta = Math.min(desde + pageSize - 1, total);
                    lblResumenPaginacion.setText("Mostrando " + desde + "-" + hasta + " de " + total + " usuarios");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
