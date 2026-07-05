package com.store.inventario.module.product.controller;

import com.store.inventario.shared.model.PageResponse;
import com.store.inventario.module.product.model.entity.Category;
import com.store.inventario.module.product.model.entity.ProductMetrics;
import com.store.inventario.module.product.model.entity.Product;
import com.store.inventario.module.product.service.CategoryService;
import com.store.inventario.security.SessionManager;
import com.store.inventario.module.product.service.ProductService;

import com.store.inventario.shared.model.NavigationManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

public class ProductController implements Initializable {

    @FXML private Button btnNuevoProducto;
    @FXML private Button btnActualizar;
    @FXML private TableView<Product> tblProductos;
    @FXML private TableColumn<Product, String> colCodigo;
    @FXML private TableColumn<Product, String> colNombre;
    @FXML private TableColumn<Product, String> colCategoria;
    @FXML private TableColumn<Product, String> colMarca;
    @FXML private TableColumn<Product, String> colModelo;
    @FXML private TableColumn<Product, BigDecimal> colPrecio;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, Integer> colGarantia;
    @FXML private TableColumn<Product, Void> colAcciones;

    @FXML private Label lblTotalProductos;
    @FXML private Label lblStockBajo;
    @FXML private Label lblAgotados;
    @FXML private Label lblCategorias;
    @FXML private Label lblResumenPaginacion;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private ComboBox<String> cbMarca;
    @FXML private ComboBox<String> cbEstado;
    @FXML private Button btnLimpiar;
    @FXML private Button btnBuscar;
    @FXML private Button btnGestionarCategorias;

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();
    private final String rolActual = SessionManager.getInstance().getRole();
    private int paginaActual = 0;
    private final int tamanoPagina = 20;
    private int totalPaginas = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("code"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("model"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colGarantia.setCellValueFactory(new PropertyValueFactory<>("warrantyMonths"));
        configurarColumnaAcciones();
        configurarFiltros();
        refreshData();

        if("SELLER".equalsIgnoreCase(rolActual)){
            btnNuevoProducto.setVisible(false);
            btnNuevoProducto.setManaged(false);
            btnGestionarCategorias.setVisible(false);
            btnGestionarCategorias.setManaged(false);
            tblProductos.getColumns().remove(colAcciones);
        }


    }

    @FXML
    private void handleForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/product/product-form.fxml"));
        Parent root = loader.load();

        Stage modal = new Stage();
        com.store.inventario.shared.utils.WindowUtils.applyIcon(modal);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Nuevo Producto");
        modal.setScene(new Scene(root));
        modal.showAndWait();

        refreshData();
    }

    @FXML
    private void handleActualizar() {
        refreshData();
    }

    @FXML
    private void handleGestionarCategorias() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/product/category-management.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            com.store.inventario.shared.utils.WindowUtils.applyIcon(modal);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Gestión de Categorías");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            refreshData();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir la gestión de categorías");
            alert.setContentText("Ocurrió un error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleEditar(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/product/product-form.fxml"));
            Parent root = loader.load();

            ProductFormController controller = loader.getController();
            controller.setProductoEditar(product);

            Stage modal = new Stage();
            com.store.inventario.shared.utils.WindowUtils.applyIcon(modal);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Editar Producto");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            refreshData();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir el formulario");
            alert.setContentText("Ocurrió un error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleEliminar(Product product) {
        javafx.application.Platform.runLater(() -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Eliminación");
            confirmacion.setHeaderText("¿Eliminar producto?");
            confirmacion.setContentText("Se eliminará \"" + product.getName() + "\" (Código: " + product.getCode() + "). Esta acción no se puede deshacer.");

            Optional<ButtonType> resultado = confirmacion.showAndWait();

            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    productService.eliminarProducto(product.getCode());

                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Éxito");
                    exito.setHeaderText("Producto Eliminado");
                    exito.setContentText("El producto se ha eliminado correctamente.");
                    exito.showAndWait();

                    refreshData();
                    NavigationManager.getInstance().refreshAlerts();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("No se pudo eliminar el producto");
                    error.setContentText("Ocurrió un error: " + e.getMessage());
                    error.showAndWait();
                }
            }
        });
    }

    private void configurarColumnaAcciones() {
        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                return new TableCell<>() {
                    private final Button btnAcciones = new Button("⋮");
                    private final ContextMenu menuAcciones = new ContextMenu();
                    private final MenuItem itemEditar = new MenuItem("Editar");
                    private final MenuItem itemEliminar = new MenuItem("Eliminar");
                    private final HBox contenedor = new HBox(btnAcciones);

                    {
                        btnAcciones.getStyleClass().add("btn-acciones");
                        btnAcciones.setTooltip(new Tooltip("Acciones de producto"));
                        
                        itemEditar.getStyleClass().add("menu-item-editar");
                        itemEliminar.getStyleClass().add("menu-item-eliminar");


                        if("ADMIN".equalsIgnoreCase(rolActual)) {
                            menuAcciones.getItems().addAll(itemEditar, itemEliminar);
                        }else{
                            menuAcciones.getItems().addAll(itemEditar);
                        }

                        contenedor.setAlignment(Pos.CENTER);

                        btnAcciones.setOnAction(event -> {
                            menuAcciones.show(btnAcciones, javafx.geometry.Side.BOTTOM, 0, 0);
                        });

                        itemEditar.setOnAction(event -> {
                            Product product = getTableView().getItems().get(getIndex());
                            handleEditar(product);
                        });

                        itemEliminar.setOnAction(event -> {
                            Product product = getTableView().getItems().get(getIndex());
                            handleEliminar(product);
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

    @FXML
    private void handlePaginaAnterior() {
        if (paginaActual > 0) {
            paginaActual--;
            obtenerProductos();
        }
    }

    @FXML
    private void handlePaginaSiguiente() {
        if (paginaActual < totalPaginas - 1) {
            paginaActual++;
            obtenerProductos();
        }
    }

    private void refreshData() {
        obtenerProductos();
        cargarMetricasYFiltrosGlobales();
    }

    private void obtenerProductos() {
        try {
            String search = (txtBuscar != null) ? txtBuscar.getText().trim() : "";
            String category = (cbCategoria != null) ? cbCategoria.getValue() : null;
            String brand = (cbMarca != null) ? cbMarca.getValue() : null;
            String status = (cbEstado != null) ? cbEstado.getValue() : null;

            PageResponse<Product> response = productService.obtenerProductos(search, category, brand, status, paginaActual, tamanoPagina);
            List<Product> products = (response != null && response.getContent() != null)
                    ? response.getContent()
                    : Collections.emptyList();

            totalPaginas = (response != null) ? response.getTotalPages() : 1;
            btnAnterior.setDisable(paginaActual == 0);
            btnSiguiente.setDisable(paginaActual >= totalPaginas - 1);

            tblProductos.setItems(FXCollections.observableArrayList(products));
            if (response != null) {
                actualizarPaginacion(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ejecutarBusqueda() {
        paginaActual = 0;
        obtenerProductos();
    }

    @FXML
    private void limpiarBusqueda() {
        if (txtBuscar != null) txtBuscar.clear();
        if (cbCategoria != null) cbCategoria.setValue("Categoría");
        if (cbMarca != null) cbMarca.setValue("Marca");
        if (cbEstado != null) cbEstado.setValue("Todos");
        paginaActual = 0;
        obtenerProductos();
    }

    private void configurarFiltros() {
        cbEstado.setItems(FXCollections.observableArrayList("Todos", "Con Stock", "Stock Bajo", "Agotados"));
        cbEstado.setValue("Todos");
    }

    private void cargarMetricasYFiltrosGlobales() {
        try {
            ProductMetrics metrics = productService.obtenerMetricas();
            PageResponse<Category> categoriesResponse = categoryService.obtenerCategorias();
            List<String> marcas = productService.obtenerMarcas();

            if (metrics != null) {
                lblTotalProductos.setText(String.valueOf(metrics.getTotalProducts()));
                lblStockBajo.setText(String.valueOf(metrics.getLowStockCount()));
                lblAgotados.setText(String.valueOf(metrics.getOutOfStockCount()));
                lblCategorias.setText(String.valueOf(metrics.getTotalCategories()));
            }

            Set<String> categoriasSet = new TreeSet<>();
            categoriasSet.add("Todas");
            if (categoriesResponse != null && categoriesResponse.getContent() != null) {
                for (Category c : categoriesResponse.getContent()) {
                    if (c != null && c.getName() != null) {
                        categoriasSet.add(c.getName());
                    }
                }
            }

            Set<String> marcasSet = new TreeSet<>();
            marcasSet.add("Todas");
            if (marcas != null) {
                for (String m : marcas) {
                    if (m != null) {
                        marcasSet.add(m);
                    }
                }
            }

            String selectedCat = cbCategoria.getValue();
            String selectedMarca = cbMarca.getValue();

            cbCategoria.setItems(FXCollections.observableArrayList(categoriasSet));
            if (selectedCat != null && categoriasSet.contains(selectedCat)) {
                cbCategoria.setValue(selectedCat);
            } else {
                cbCategoria.setValue("Categoría");
            }

            cbMarca.setItems(FXCollections.observableArrayList(marcasSet));
            if (selectedMarca != null && marcasSet.contains(selectedMarca)) {
                cbMarca.setValue(selectedMarca);
            } else {
                cbMarca.setValue("Marca");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizarPaginacion(PageResponse<Product> activeResponse) {
        long total = activeResponse.getTotalElements();
        int paginas = activeResponse.getTotalPages();
        int paginaActual = activeResponse.getNumber();
        int pageSize = activeResponse.getSize();
        
        if (total == 0) {
            lblResumenPaginacion.setText("No hay productos para mostrar");
            return;
        }
        
        long desde = (long) paginaActual * pageSize + 1;
        long hasta = Math.min(desde + pageSize - 1, total);
        
        lblResumenPaginacion.setText("Mostrando " + desde + "-" + hasta + " de " + total + " productos (Página " + (paginaActual + 1) + " de " + paginas + ")");
    }
}