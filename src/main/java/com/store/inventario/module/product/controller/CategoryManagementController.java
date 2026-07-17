package com.store.inventario.module.product.controller;

import com.store.inventario.shared.model.PageResponse;
import com.store.inventario.module.product.model.entity.Category;
import com.store.inventario.security.SessionManager;
import com.store.inventario.module.product.service.CategoryService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.List;
import java.util.Optional;

public class CategoryManagementController {

    @FXML
    private TableView<Category> tblCategorias;
    @FXML
    private TableColumn<Category, String> colCodigo;
    @FXML
    private TableColumn<Category, String> colNombre;
    @FXML
    private TableColumn<Category, Void> colAcciones;

    @FXML
    private TextField txtNombre;
    @FXML
    private Button btnCancelarEdicion;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCerrar;

    @FXML
    private HBox contenedorCrearCategoria;


    private final CategoryService categoryService = new CategoryService();
    private Category categoryEditar = null;

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("code"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("name"));

        configurarColumnaAcciones();
        cargarCategorias();
        com.store.inventario.shared.utils.TableUtils.habilitarDobleClicParaCopiar(tblCategorias);

        String roleActual = SessionManager.getInstance().getUser().getRole();

        if(!"ADMIN".equalsIgnoreCase(roleActual)){
            contenedorCrearCategoria.setVisible(false);
            contenedorCrearCategoria.setManaged(false);
            tblCategorias.getColumns().remove(colAcciones);
        }
    }

    private void configurarColumnaAcciones() {
        Callback<TableColumn<Category, Void>, TableCell<Category, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Category, Void> call(final TableColumn<Category, Void> param) {
                return new TableCell<>() {
                    private final Button btnAcciones = new Button("⋮");
                    private final ContextMenu menuAcciones = new ContextMenu();
                    private final MenuItem itemEditar = new MenuItem("Editar");
                    private final MenuItem itemEliminar = new MenuItem("Eliminar");
                    private final HBox contenedor = new HBox(btnAcciones);

                    {
                        btnAcciones.getStyleClass().add("btn-acciones");
                        btnAcciones.setTooltip(new Tooltip("Acciones"));

                        itemEditar.getStyleClass().add("menu-item-editar");
                        itemEliminar.getStyleClass().add("menu-item-eliminar");
                        menuAcciones.getItems().addAll(itemEditar, itemEliminar);

                        contenedor.setAlignment(Pos.CENTER);

                        btnAcciones.setOnAction(event -> {
                            menuAcciones.show(btnAcciones, javafx.geometry.Side.BOTTOM, 0, 0);
                        });

                        itemEditar.setOnAction(event -> {
                            Category cat = getTableView().getItems().get(getIndex());
                            handleEditar(cat);
                        });

                        itemEliminar.setOnAction(event -> {
                            Category cat = getTableView().getItems().get(getIndex());
                            handleEliminar(cat);
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

    private void cargarCategorias() {
        try {
            PageResponse<Category> response = categoryService.obtenerCategorias();
            List<Category> lista = (response != null) ? response.getContent() : java.util.Collections.emptyList();
            tblCategorias.setItems(FXCollections.observableArrayList(lista));
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron cargar las categorías del servidor.");
        }
    }

    @FXML
    private void handleGuardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Por favor, ingrese el nombre de la categoría.");
            return;
        }

        String codigoExcluir = (categoryEditar != null) ? categoryEditar.getCode() : null;
        if (existeCategoriaConNombre(nombre, codigoExcluir)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Ya existe una categoría con el nombre '" + nombre + "'.");
            return;
        }

        try {
            if (categoryEditar == null) {
                // Crear Categoría
                Category nueva = new Category(null, nombre);
                categoryService.crearCategoria(nueva);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Categoría creada correctamente.");
            } else {
                // Editar Categoría
                Category actualizada = new Category(categoryEditar.getCode(), nombre);
                categoryService.actualizarCategoria(categoryEditar.getCode(), actualizada);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Categoría actualizada correctamente.");
            }

            cargarCategorias();
            handleCancelarEdicion();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un error al guardar los cambios: " + e.getMessage());
        }
    }

    private boolean existeCategoriaConNombre(String nombre, String codigoExcluir) {
        if (tblCategorias.getItems() == null) {
            return false;
        }
        return tblCategorias.getItems().stream()
                .anyMatch(c -> c.getName() != null && 
                               c.getName().trim().equalsIgnoreCase(nombre.trim()) && 
                               (codigoExcluir == null || !c.getCode().equals(codigoExcluir)));
    }

    private void handleEditar(Category cat) {
        categoryEditar = cat;
        txtNombre.setText(cat.getName());
        btnGuardar.setText("Actualizar");
        btnCancelarEdicion.setVisible(true);
        btnCancelarEdicion.setManaged(true);
    }

    private void handleEliminar(Category cat) {
        javafx.application.Platform.runLater(() -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Eliminación");
            confirmacion.setHeaderText("¿Eliminar categoría?");
            confirmacion.setContentText("Se eliminará la categoría \"" + cat.getName() + "\" (Código: " + cat.getCode() + "). Esta acción no se puede deshacer.");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    categoryService.eliminarCategoria(cat.getCode());
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "La categoría se ha eliminado correctamente.");
                    cargarCategorias();
                    if (categoryEditar != null && categoryEditar.getCode().equals(cat.getCode())) {
                        handleCancelarEdicion();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar la categoría. Asegúrese de que no tenga productos asociados: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleCancelarEdicion() {
        categoryEditar = null;
        txtNombre.clear();
        btnGuardar.setText("Guardar");
        btnCancelarEdicion.setVisible(false);
        btnCancelarEdicion.setManaged(false);
    }

    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        com.store.inventario.shared.utils.WindowUtils.applyIcon(alerta);
        alerta.showAndWait();
    }
}
