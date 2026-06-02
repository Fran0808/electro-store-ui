package com.store.inventario.controller.productos;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.producto.Producto;
import com.store.inventario.security.SessionManager;
import com.store.inventario.service.producto.ProductoService;

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
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class ProductoController implements Initializable {

    @FXML
    private Button btnNuevoProducto;
    @FXML
    private Button btnActualizar;
    @FXML
    private TableView<Producto> tblProductos;
    @FXML
    private TableColumn<Producto, String> colCodigo;
    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private TableColumn<Producto, String> colCategoria;
    @FXML
    private TableColumn<Producto, String> colMarca;
    @FXML
    private TableColumn<Producto, String> colModelo;
    @FXML
    private TableColumn<Producto, BigDecimal> colPrecio;
    @FXML
    private TableColumn<Producto, Integer> colStock;
    @FXML
    private TableColumn<Producto, Integer> colGarantia;
    @FXML
    private TableColumn<Producto, Void> colAcciones;

    @FXML
    private Label lblTotalProductos;
    @FXML
    private Label lblStockBajo;
    @FXML
    private Label lblAgotados;
    @FXML
    private Label lblCategorias;
    @FXML
    private Label lblResumenPaginacion;
    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnSiguiente;
    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cbCategoria;
    @FXML
    private ComboBox<String> cbMarca;
    @FXML
    private ComboBox<String> cbEstado;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnBuscar;

    private final ProductoService productoService = new ProductoService();

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
        obtenerProductos();
    }

    @FXML
    private void handleForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/productos/producto-form.fxml"));
        Parent root = loader.load();

        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Nuevo Producto");
        modal.setScene(new Scene(root));
        modal.showAndWait();

        obtenerProductos();
    }

    @FXML
    private void handleActualizar() {
        obtenerProductos();
    }

    @FXML
    private void handleGestionarCategorias() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/productos/gestion-categorias.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Gestión de Categorías");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            obtenerProductos();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir la gestión de categorías");
            alert.setContentText("Ocurrió un error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleEditar(Producto producto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/productos/producto-form.fxml"));
            Parent root = loader.load();

            ProductoFormController controller = loader.getController();
            controller.setProductoEditar(producto);

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Editar Producto");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            obtenerProductos();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir el formulario");
            alert.setContentText("Ocurrió un error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleEliminar(Producto producto) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar producto?");
        confirmacion.setContentText("Se eliminará \"" + producto.getName() + "\" (Código: " + producto.getCode() + "). Esta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                productoService.eliminarProducto(producto.getCode());

                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Éxito");
                exito.setHeaderText("Producto Eliminado");
                exito.setContentText("El producto se ha eliminado correctamente.");
                exito.showAndWait();

                obtenerProductos();
            } catch (Exception e) {
                e.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("No se pudo eliminar el producto");
                error.setContentText("Ocurrió un error: " + e.getMessage());
                error.showAndWait();
            }
        }
    }

    private void configurarColumnaAcciones() {
        Callback<TableColumn<Producto, Void>, TableCell<Producto, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Producto, Void> call(final TableColumn<Producto, Void> param) {
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

                        String rolActual = SessionManager.getInstance().getRole();
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
                            Producto producto = getTableView().getItems().get(getIndex());
                            handleEditar(producto);
                        });

                        itemEliminar.setOnAction(event -> {
                            Producto producto = getTableView().getItems().get(getIndex());
                            handleEliminar(producto);
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

    private void obtenerProductos() {
        try {
            PageResponse<Producto> response = productoService.obtenerProductos();
            List<Producto> productos = response.getContent();

            tblProductos.setItems(FXCollections.observableArrayList(productos));

            actualizarFiltrosDinamicos(productos);

            actualizarMetricas(response, productos);
            actualizarPaginacion(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configurarFiltros() {
        cbEstado.setItems(FXCollections.observableArrayList("Todos", "Con Stock", "Stock Bajo", "Agotados"));
        cbEstado.setValue("Todos");
    }

    private void actualizarFiltrosDinamicos(List<Producto> productos) {
        // Extraer categorías únicas
        Set<String> categoriasSet = new TreeSet<>();
        categoriasSet.add("Todas");
        // Extraer marcas únicas
        Set<String> marcasSet = new TreeSet<>();
        marcasSet.add("Todas");

        for (Producto p : productos) {
            if (p.getCategoryName() != null) categoriasSet.add(p.getCategoryName());
            if (p.getBrand() != null) marcasSet.add(p.getBrand());
        }

        cbCategoria.setItems(FXCollections.observableArrayList(categoriasSet));
        cbCategoria.setValue("Categoría");

        cbMarca.setItems(FXCollections.observableArrayList(marcasSet));
        cbMarca.setValue("Marca");
    }

    private void actualizarMetricas(PageResponse<Producto> response, List<Producto> productos) {
        lblTotalProductos.setText(String.valueOf(response.getTotalElements()));

        long stockBajo = productos.stream()
                .filter(p -> p.getStock() != null && p.getStock() > 0 && p.getStock() < 10)
                .count();
        lblStockBajo.setText(String.valueOf(stockBajo));

        long agotados = productos.stream()
                .filter(p -> p.getStock() != null && p.getStock() == 0)
                .count();
        lblAgotados.setText(String.valueOf(agotados));

        long categorias = productos.stream()
                .map(Producto::getCategoryName)
                .distinct()
                .count();
        lblCategorias.setText(String.valueOf(categorias));
    }

    private void actualizarPaginacion(PageResponse<Producto> activeResponse) {
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