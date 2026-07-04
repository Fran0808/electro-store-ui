package com.store.inventario.controller.proveedor;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.proveedor.Proveedor;
import com.store.inventario.service.proveedor.ProveedorService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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

public class ProveedorSearchModalController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Proveedor> tblProveedores;
    @FXML private TableColumn<Proveedor, String> colCodigo;
    @FXML private TableColumn<Proveedor, String> colNombreComercial;
    @FXML private TableColumn<Proveedor, String> colRazonSocial;
    @FXML private TableColumn<Proveedor, String> colRuc;
    @FXML private TableColumn<Proveedor, String> colTelefono;
    @FXML private Label lblResumenPaginacion;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Button btnCancelar;
    @FXML private Button btnSeleccionar;

    private final ProveedorService proveedorService = new ProveedorService();
    private Proveedor proveedorSeleccionado = null;
    
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
                PageResponse<Proveedor> response = proveedorService.listar(search, paginaActual, tamanoPagina);
                List<Proveedor> proveedores = (response != null && response.getContent() != null)
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
        Proveedor selected = tblProveedores.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atención");
            alert.setHeaderText(null);
            alert.setContentText("Debe seleccionar un proveedor de la lista.");
            alert.showAndWait();
            return;
        }
        this.proveedorSeleccionado = selected;
        cerrarModal();
    }

    @FXML
    private void handleCancelar() {
        this.proveedorSeleccionado = null;
        cerrarModal();
    }

    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    public Proveedor getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }
}
