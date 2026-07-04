package com.store.inventario.controller.proveedor;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.proveedor.Proveedor;
import com.store.inventario.security.SessionManager;
import com.store.inventario.service.proveedor.ProveedorService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProveedorController implements Initializable {

    @FXML
    private TableView<Proveedor> tblProveedores;

    @FXML
    private TableColumn<Proveedor, String> colCodigo;

    @FXML
    private TableColumn<Proveedor, String> colRuc;

    @FXML
    private TableColumn<Proveedor, String> colNombreComercial;

    @FXML
    private TableColumn<Proveedor, String> colRazonSocial;

    @FXML
    private TableColumn<Proveedor, String> colTelefono;

    @FXML
    private TableColumn<Proveedor, Void> colAcciones;

    @FXML
    private Label lblTotalProveedor;

    @FXML
    private Label lblProveedorFrecuente;

    @FXML
    private Label lblUltimoRegistrado;

    @FXML
    private Label lblResumenPaginacion;

    @FXML
    private TextField txtBuscar;

    @FXML
    private Button btnAnterior;

    @FXML
    private Button btnSiguiente;

    private final ProveedorService proveedorService =
            new ProveedorService();
    private int paginaActual = 0;
    private final int tamanoPagina = 10;
    private int totalPaginas = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        colCodigo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCode()));

        colRuc.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTaxId()));

        colNombreComercial.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTradeName()));

        colRazonSocial.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getLegalName()));

        colTelefono.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPhone()));

        if (btnAnterior != null) {
            btnAnterior.setOnAction(e -> handlePaginaAnterior());
        }
        if (btnSiguiente != null) {
            btnSiguiente.setOnAction(e -> handlePaginaSiguiente());
        }

        configurarColumnaAcciones();

        obtenerProveedores();
    }

    @FXML
    private void handleForm() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/proveedores/proveedor-form.fxml"));
        Parent root = loader.load();
        Stage modal = new Stage();
        com.store.inventario.utils.WindowUtils.applyIcon(modal);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Nuevo Proveedor");
        modal.setResizable(false);
        modal.setScene(new Scene(root));
        modal.showAndWait();
        obtenerProveedores();
    }

    private void handleEditar(Proveedor proveedor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/proveedores/proveedor-form.fxml"));
            Parent root = loader.load();
            ProveedorFormController controller = loader.getController();
            controller.setProveedorEditar(proveedor);
            Stage modal = new Stage();
            com.store.inventario.utils.WindowUtils.applyIcon(modal);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Editar Proveedor");
            modal.setResizable(false);
            modal.setScene(new Scene(root));
            modal.showAndWait();
            obtenerProveedores();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir el formulario");
            alert.setContentText(e.getMessage());
            com.store.inventario.utils.WindowUtils.applyIcon(alert);
            alert.showAndWait();
        }
    }

    private void handleEliminar(Proveedor proveedor) {
        javafx.application.Platform.runLater(() -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Eliminación");
            confirmacion.setHeaderText("¿Eliminar proveedor?");
            confirmacion.setContentText("Se eliminará el proveedor \"" + proveedor.getTradeName() + "\"");
            com.store.inventario.utils.WindowUtils.applyIcon(confirmacion);
            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    proveedorService.eliminar(proveedor.getCode());
                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Éxito");
                    exito.setHeaderText("Proveedor eliminado");
                    exito.setContentText("El proveedor se eliminó correctamente");
                    com.store.inventario.utils.WindowUtils.applyIcon(exito);
                    exito.showAndWait();
                    obtenerProveedores();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("No se pudo eliminar el proveedor");
                    error.setContentText(e.getMessage());
                    com.store.inventario.utils.WindowUtils.applyIcon(error);
                    error.showAndWait();
                }
            }
        });
    }

    private void configurarColumnaAcciones() {
        Callback<TableColumn<Proveedor, Void>, TableCell<Proveedor, Void>> cellFactory = new Callback<>() {
                    @Override
                    public TableCell<Proveedor, Void> call(final TableColumn<Proveedor, Void> param) {
                        return new TableCell<>() {
                            private final Button btnAcciones = new Button("⋮");
                            private final ContextMenu menuAcciones = new ContextMenu();
                            private final MenuItem itemEditar = new MenuItem("Editar");
                            private final MenuItem itemEliminar = new MenuItem("Eliminar");
                            private final HBox contenedor = new HBox(btnAcciones);
                            {
                                btnAcciones.getStyleClass().add("btn-acciones");
                                contenedor.setAlignment(Pos.CENTER);

                                String rolActual = SessionManager.getInstance().getRole();

                                if(!"ADMIN".equalsIgnoreCase(rolActual)){
                                    menuAcciones.getItems().addAll(itemEditar);
                                }else{
                                    menuAcciones.getItems().addAll(itemEditar, itemEliminar);
                                }

                                btnAcciones.setOnAction(event -> {
                                    menuAcciones.show(
                                            btnAcciones,
                                            javafx.geometry.Side.BOTTOM,
                                            0,
                                            0
                                    );
                                });

                                itemEditar.setOnAction(event -> {
                                    Proveedor proveedor = getTableView().getItems().get(getIndex());
                                    handleEditar(proveedor);
                                });
                                itemEliminar.setOnAction(event -> {
                                    Proveedor proveedor = getTableView().getItems().get(getIndex());
                                    handleEliminar(proveedor);
                                });
                            }

                            @Override
                            protected void updateItem(
                                    Void item,
                                    boolean empty
                            ) {
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

    private void obtenerProveedores() {
        try {
            String search = (txtBuscar != null) ? txtBuscar.getText().trim() : "";
            PageResponse<Proveedor> response = proveedorService.listar(search, paginaActual, tamanoPagina);
            List<Proveedor> proveedores = (response != null && response.getContent() != null)
                    ? response.getContent()
                    : java.util.Collections.emptyList();
            tblProveedores.setItems(FXCollections.observableArrayList(proveedores));

            if (response != null) {
                totalPaginas = response.getTotalPages();
            }

            actualizarMetricas(response);
            actualizarPaginacion(response);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudieron obtener los proveedores");
            alert.setContentText(e.getMessage());
            com.store.inventario.utils.WindowUtils.applyIcon(alert);
            alert.showAndWait();
        }
    }

    private void actualizarMetricas(PageResponse<Proveedor> response) {
        try {
            com.store.inventario.model.proveedor.SupplierMetrics metrics = proveedorService.obtenerMetricas();
            if (metrics != null) {
                lblTotalProveedor.setText(String.valueOf(metrics.getTotalSuppliers()));
                lblUltimoRegistrado.setText(metrics.getLastSupplierName() != null ? metrics.getLastSupplierName() : "Ninguno");
            } else {
                lblTotalProveedor.setText(String.valueOf(response.getTotalElements()));
                lblUltimoRegistrado.setText("Ninguno");
            }
        } catch (Exception e) {
            e.printStackTrace();
            lblTotalProveedor.setText(String.valueOf(response.getTotalElements()));
            lblUltimoRegistrado.setText("Ninguno");
        }

        try {
            com.store.inventario.service.compra.CompraService compraService = new com.store.inventario.service.compra.CompraService();
            com.store.inventario.model.compra.PurchaseMetrics purchaseMetrics = compraService.obtenerMetricas();
            if (purchaseMetrics != null && purchaseMetrics.getFrequentSupplierName() != null && !purchaseMetrics.getFrequentSupplierName().trim().isEmpty()) {
                lblProveedorFrecuente.setText(purchaseMetrics.getFrequentSupplierName());
            } else {
                lblProveedorFrecuente.setText("Ninguno");
            }
        } catch (Exception e) {
            lblProveedorFrecuente.setText("Ninguno");
            e.printStackTrace();
        }
    }

    private void actualizarPaginacion(PageResponse<Proveedor> response) {
        if (response == null) return;
        long total = response.getTotalElements();
        int pageNum = response.getNumber();
        int totalPages = response.getTotalPages();
        int size = response.getSize();
        int contentSize = (response.getContent() != null) ? response.getContent().size() : 0;

        if (total == 0) {
            lblResumenPaginacion.setText("No hay proveedores para mostrar");
        } else {
            long desde = (long) pageNum * size + 1;
            long hasta = Math.min(desde + contentSize - 1, total);
            lblResumenPaginacion.setText("Mostrando " + desde + "-" + hasta + " de " + total + " proveedores (Página " + (pageNum + 1) + " de " + totalPages + ")");
        }

        btnAnterior.setDisable(pageNum == 0);
        btnSiguiente.setDisable(pageNum >= totalPages - 1);
    }

    @FXML
    private void ejecutarBusqueda() {
        paginaActual = 0;
        obtenerProveedores();
    }

    @FXML
    private void limpiarFiltros() {
        if (txtBuscar != null) {
            txtBuscar.clear();
        }
        paginaActual = 0;
        obtenerProveedores();
    }

    @FXML
    private void handlePaginaAnterior() {
        if (paginaActual > 0) {
            paginaActual--;
            obtenerProveedores();
        }
    }

    @FXML
    private void handlePaginaSiguiente() {
        if (paginaActual < totalPaginas - 1) {
            paginaActual++;
            obtenerProveedores();
        }
    }

    @FXML
    private void handleActualizar() {
        obtenerProveedores();
    }
}