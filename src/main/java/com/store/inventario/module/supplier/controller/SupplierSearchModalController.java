package com.store.inventario.module.supplier.controller;

import com.store.inventario.model.PageResponse;
import com.store.inventario.module.supplier.model.entity.Supplier;
import com.store.inventario.module.supplier.service.SupplierService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SupplierSearchModalController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Supplier> tblProveedores;
    @FXML private TableColumn<Supplier, String> colCodigo;
    @FXML private TableColumn<Supplier, String> colNombreComercial;
    @FXML private TableColumn<Supplier, String> colRazonSocial;
    @FXML private TableColumn<Supplier, String> colRuc;
    @FXML private TableColumn<Supplier, String> colTelefono;
    @FXML private Label lblResumenPaginacion;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Button btnCancelar;
    @FXML private Button btnSeleccionar;

    private final SupplierService supplierService = new SupplierService();
    private Supplier supplierSeleccionado = null;
    
    private int paginaActual = 0;
    private final int tamanoPagina = 10;
    private int totalPaginas = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("code"));
        colNombreComercial.setCellValueFactory(new PropertyValueFactory<>("tradeName"));
        colRazonSocial.setCellValueFactory(new PropertyValueFactory<>("legalName"));
        colRuc.setCellValueFactory(new PropertyValueFactory<>("taxId"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("phone"));

        obtenerProveedores();
    }

    private void obtenerProveedores() {
        Platform.runLater(() -> {
            try {
                String search = txtBuscar.getText().trim();
                PageResponse<Supplier> response = supplierService.listar(search, paginaActual, tamanoPagina);
                List<Supplier> proveedores = (response != null && response.getContent() != null)
                        ? response.getContent()
                        : java.util.Collections.emptyList();

                tblProveedores.setItems(FXCollections.observableArrayList(proveedores));

                if (response != null) {
                    totalPaginas = response.getTotalPages();
                    btnAnterior.setDisable(paginaActual == 0);
                    btnSiguiente.setDisable(paginaActual >= totalPaginas - 1);

                    long total = response.getTotalElements();
                    int paginas = response.getTotalPages();
                    int pageNum = response.getNumber();
                    int pageSize = response.getSize();

                    if (total == 0) {
                        lblResumenPaginacion.setText("No hay proveedores para mostrar");
                    } else {
                        long desde = (long) pageNum * pageSize + 1;
                        long hasta = Math.min(desde + proveedores.size() - 1, total);
                        lblResumenPaginacion.setText("Mostrando " + desde + "-" + hasta + " de " + total + " proveedores (Página " + (pageNum + 1) + " de " + paginas + ")");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleBuscar() {
        paginaActual = 0;
        obtenerProveedores();
    }

    @FXML
    private void handleLimpiar() {
        txtBuscar.clear();
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
    private void handleTablaClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            handleSeleccionar();
        }
    }

    @FXML
    private void handleSeleccionar() {
        Supplier selected = tblProveedores.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atención");
            alert.setHeaderText(null);
            alert.setContentText("Debe seleccionar un proveedor de la lista.");
            com.store.inventario.utils.WindowUtils.applyIcon(alert);
            alert.showAndWait();
            return;
        }
        this.supplierSeleccionado = selected;
        cerrarModal();
    }

    @FXML
    private void handleCancelar() {
        this.supplierSeleccionado = null;
        cerrarModal();
    }

    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    public Supplier getProveedorSeleccionado() {
        return supplierSeleccionado;
    }
}
