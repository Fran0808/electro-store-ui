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
    private Label lblUltimoRegristrado;

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
            alert.showAndWait();
        }
    }

    private void handleEliminar(Proveedor proveedor) {
        javafx.application.Platform.runLater(() -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Eliminación");
            confirmacion.setHeaderText("¿Eliminar proveedor?");
            confirmacion.setContentText("Se eliminará el proveedor \"" + proveedor.getTradeName() + "\"");
            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    proveedorService.eliminar(proveedor.getCode());
                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Éxito");
                    exito.setHeaderText("Proveedor eliminado");
                    exito.setContentText("El proveedor se eliminó correctamente");
                    exito.showAndWait();
                    obtenerProveedores();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("No se pudo eliminar el proveedor");
                    error.setContentText(e.getMessage());
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
            PageResponse<Proveedor> response = proveedorService.listar(0, 10);
            List<Proveedor> proveedores = response.getContent();
            tblProveedores.setItems(FXCollections.observableArrayList(proveedores));
            actualizarMetricas(response);
            actualizarPaginacion(response);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudieron obtener los proveedores");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void actualizarMetricas(PageResponse<Proveedor> response) {
        lblTotalProveedor.setText(String.valueOf(response.getTotalElements()));

        List<Proveedor> proveedores = response.getContent();
        if (proveedores != null && !proveedores.isEmpty()) {
            Proveedor ultimo = proveedores.get(proveedores.size() - 1);
            lblUltimoRegristrado.setText(ultimo.getTradeName());
        } else {
            lblUltimoRegristrado.setText("Ninguno");
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
        long total = response.getTotalElements();
        int paginaActual = response.getNumber();
        int totalPaginas = response.getTotalPages();
        int size = response.getSize();
        long desde = (long) paginaActual * size + 1;
        long hasta = Math.min(desde + size - 1, total);

        lblResumenPaginacion.setText("Mostrando " + desde + "-" + hasta + " de " + total + " proveedores");
        btnAnterior.setDisable(paginaActual == 0);
        btnSiguiente.setDisable(
                paginaActual >= totalPaginas - 1
        );
    }
}