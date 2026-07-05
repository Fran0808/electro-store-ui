package com.store.inventario.module.auth.controller;

import com.store.inventario.shared.model.PageResponse;
import com.store.inventario.module.auth.model.entity.User;
import com.store.inventario.module.auth.service.UserService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class UserController {
    @FXML private TextField txtBuscar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnBuscar;
    @FXML private Button btnAnterior;
    @FXML public Button btnSiguiente;
    
    private final ObservableList<User> masterData = FXCollections.observableArrayList();
    private javafx.collections.transformation.FilteredList<User> filteredData;
    private int paginaActual = 0;
    private final int tamanoPagina = 30;
    private int totalPaginas = 1;
    @FXML private TableView<User> tblUsuarios;
    @FXML private TableColumn<User, String> colCodigo;
    @FXML private TableColumn<User, String> colUsuario;
    @FXML private TableColumn<User, String> colRol;
    @FXML private TableColumn<User, String> colCodigoEmpleado;
    @FXML private TableColumn<User, String> colNombre;
    @FXML private TableColumn<User, String> colApellido;
    @FXML private TableColumn<User, Void> colAcciones;
    @FXML private TableColumn<User, Boolean> colEstado;
    
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblTotalAdmins;
    @FXML private Label lblTotalOperativos;
    @FXML private Label lblResumenPaginacion;

    private final UserService userService = new UserService();

    @FXML
    public void abrirModalNuevoUsuario() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/auth/user-form.fxml"));
        Parent root = loader.load();
        
        Stage modal = new Stage();
        com.store.inventario.shared.utils.WindowUtils.applyIcon(modal);
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
                    case "SELLER": rolEspanol = "VENDEDOR"; break;
                    case "STOREKEEPER": rolEspanol = "ALMACENERO"; break;
                    default: rolEspanol = rolIngles.toUpperCase().trim();
                }
            }
            return new javafx.beans.property.SimpleStringProperty(rolEspanol);
        });
        colCodigoEmpleado.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        configurarColumnaEstado();
        configurarColumnaAcciones();

        filteredData = new javafx.collections.transformation.FilteredList<>(masterData, p -> true);
        tblUsuarios.setItems(filteredData);

        btnAnterior.setOnAction(e -> handlePaginaAnterior());
        btnSiguiente.setOnAction(e -> handlePaginaSiguiente());

        obtenerUsuarios();
    }

    private void configurarColumnaEstado() {
        colEstado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isStatus()));
        colEstado.setCellFactory(new Callback<>() {
            @Override
            public TableCell<User, Boolean> call(TableColumn<User, Boolean> param) {
                return new TableCell<>() {
                    private final Label lblStatus = new Label();
                    private final HBox contenedor = new HBox(lblStatus);
                    {
                        contenedor.setAlignment(Pos.CENTER);
                    }

                    @Override
                    protected void updateItem(Boolean status, boolean empty) {
                        super.updateItem(status, empty);
                        if (empty || status == null) {
                            setGraphic(null);
                        } else {
                            lblStatus.setText(status ? "Activo" : "Inactivo");
                            if (status) {
                                lblStatus.setStyle("-fx-background-color: rgba(74,222,128,0.15); -fx-text-fill: #16A34A; -fx-font-weight: bold; -fx-padding: 3 10 3 10; -fx-background-radius: 7px;");
                            } else {
                                lblStatus.setStyle("-fx-background-color: rgba(239,68,68,0.15); -fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-padding: 3 10 3 10; -fx-background-radius: 7px;");
                            }
                            setGraphic(contenedor);
                        }
                    }
                };
            }
        });
    }

    private void configurarColumnaAcciones() {
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button btnAcciones = new Button("⋮");
                    private final ContextMenu menuAcciones = new ContextMenu();
                    private final MenuItem itemEditar = new MenuItem("Editar");
                    private final HBox contenedor = new HBox(btnAcciones);

                    {
                        btnAcciones.getStyleClass().add("btn-acciones");
                        btnAcciones.setTooltip(new Tooltip("Acciones de Usuario"));

                        itemEditar.getStyleClass().add("menu-item-editar");

                        contenedor.setAlignment(Pos.CENTER);

                        btnAcciones.setOnAction(event -> {
                            menuAcciones.show(btnAcciones, javafx.geometry.Side.BOTTOM, 0, 0);
                        });

                        itemEditar.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleEditar(user);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            User user = getTableView().getItems().get(getIndex());
                            
                            menuAcciones.getItems().clear();
                            menuAcciones.getItems().add(itemEditar);
                            
                            MenuItem itemActivarDesactivar = new MenuItem(user.isStatus() ? "Desactivar" : "Activar");
                            itemActivarDesactivar.getStyleClass().add(user.isStatus() ? "menu-item-eliminar" : "menu-item-editar");
                            itemActivarDesactivar.setOnAction(event -> {
                                handleActivarDesactivar(user);
                            });
                            menuAcciones.getItems().add(itemActivarDesactivar);
                            
                            setGraphic(contenedor);
                        }
                    }
                };
            }
        };
        colAcciones.setCellFactory(cellFactory);
    }

    private void handleActivarDesactivar(User user) {
        Platform.runLater(() -> {
            boolean active = user.isStatus();
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle(active ? "Confirmar Desactivación" : "Confirmar Activación");
            confirmacion.setHeaderText(active ? "Desactivar usuario" : "Activar usuario");
            confirmacion.setContentText(active 
                ? "¿Está seguro de que desea desactivar al usuario " + user.getUsername() + "? No podrá iniciar sesión hasta ser reactivado."
                : "¿Está seguro de que desea activar al usuario " + user.getUsername() + "?");

            Optional<ButtonType> resultado = confirmacion.showAndWait();

            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    if (active) {
                        userService.desactivarUsuario(user.getCode());
                    } else {
                        userService.activarUsuario(user.getCode());
                    }

                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Éxito");
                    exito.setHeaderText(active ? "Usuario Desactivado" : "Usuario Activado");
                    exito.setContentText("El usuario se ha " + (active ? "desactivado" : "activado") + " correctamente.");
                    exito.showAndWait();

                    obtenerUsuarios();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("No se pudo cambiar el estado");
                    error.setContentText("Error al cambiar el estado del usuario: " + e.getMessage());
                    error.showAndWait();
                }
            }
        });
    }

    private void handleEditar(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/auth/user-form.fxml"));
            Parent root = loader.load();

            UserFormController controller = loader.getController();
            controller.setUsuarioEditar(user);

            Stage modal = new Stage();
            com.store.inventario.shared.utils.WindowUtils.applyIcon(modal);
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

    /* Por si se vuelva a necesitar este metodo
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
    */

    private void obtenerUsuarios() {
        try {
            PageResponse<User> response = userService.obtenerUsuarios(paginaActual, tamanoPagina);
            List<User> users = (response != null) ? response.getContent() : java.util.Collections.emptyList();
            masterData.setAll(users);
            
            totalPaginas = response != null ? response.getTotalPages() : 1;
            btnAnterior.setDisable(paginaActual == 0);
            btnSiguiente.setDisable(paginaActual >= totalPaginas - 1);
            
            if (lblTotalUsuarios != null) {
                lblTotalUsuarios.setText(String.valueOf(response != null ? response.getTotalElements() : users.size()));
            }
            
            if (lblTotalAdmins != null) {
                long totalAdmins = users.stream()
                        .filter(u -> u.getRole() != null && "ADMIN".equalsIgnoreCase(u.getRole().trim()))
                        .count();
                lblTotalAdmins.setText(String.valueOf(totalAdmins));
            }
            
            if (lblTotalOperativos != null) {
                long totalOperativos = users.stream()
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

    @FXML
    private void ejecutarBusqueda() {
        String text = txtBuscar.getText();
        if (text == null || text.trim().isEmpty()) {
            filteredData.setPredicate(p -> true);
        } else {
            String lowerCaseFilter = text.toLowerCase().trim();
            filteredData.setPredicate(usuario -> {
                if (usuario.getUsername() != null && usuario.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (usuario.getFirstName() != null && usuario.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (usuario.getLastName() != null && usuario.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (usuario.getRole() != null && usuario.getRole().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        }
        actualizarPaginacionConFiltrados();
    }

    @FXML
    private void limpiarBusqueda() {
        txtBuscar.clear();
        filteredData.setPredicate(p -> true);
        actualizarPaginacionConFiltrados();
    }

    private void actualizarPaginacionConFiltrados() {
        int total = filteredData.size();
        if (total == 0) {
            lblResumenPaginacion.setText("No hay usuarios para mostrar");
            return;
        }
        lblResumenPaginacion.setText("Mostrando 1-" + total + " de " + total + " usuarios");
    }

    private void handlePaginaAnterior() {
        if (paginaActual > 0) {
            paginaActual--;
            obtenerUsuarios();
        }
    }

    private void handlePaginaSiguiente() {
        if (paginaActual < totalPaginas - 1) {
            paginaActual++;
            obtenerUsuarios();
        }
    }
}
