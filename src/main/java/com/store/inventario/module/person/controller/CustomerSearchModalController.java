package com.store.inventario.module.person.controller;

import com.store.inventario.model.PageResponse;
import com.store.inventario.module.person.model.entity.Customer;
import com.store.inventario.module.person.service.CustomerService;
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

public class CustomerSearchModalController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Customer> tblClientes;
    @FXML private TableColumn<Customer, String> colCodigo;
    @FXML private TableColumn<Customer, String> colNombre;
    @FXML private TableColumn<Customer, String> colDni;
    @FXML private TableColumn<Customer, String> colRuc;
    @FXML private Label lblResumenPaginacion;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Button btnCancelar;
    @FXML private Button btnSeleccionar;

    private final CustomerService customerService = new CustomerService();
    private Customer customerSeleccionado = null;
    
    private int paginaActual = 0;
    private final int tamanoPagina = 10;
    private int totalPaginas = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("code"));
        
        colNombre.setCellValueFactory(cellData -> {
            var person = cellData.getValue().getPerson();
            return new SimpleStringProperty(person != null ? person.getFirstName() + " " + person.getLastName() : "");
        });
        
        colDni.setCellValueFactory(cellData -> {
            var person = cellData.getValue().getPerson();
            return new SimpleStringProperty(person != null && person.getNationalId() != null ? person.getNationalId() : "");
        });
        
        colRuc.setCellValueFactory(cellData -> {
            String taxId = cellData.getValue().getTaxId();
            return new SimpleStringProperty(taxId != null ? taxId : "");
        });

        obtenerClientes();
    }

    private void obtenerClientes() {
        Platform.runLater(() -> {
            try {
                String search = txtBuscar.getText().trim();
                PageResponse<Customer> response = customerService.obtenerClientes(search, paginaActual, tamanoPagina);
                List<Customer> customers = (response != null && response.getContent() != null)
                        ? response.getContent()
                        : java.util.Collections.emptyList();

                tblClientes.setItems(FXCollections.observableArrayList(customers));

                if (response != null) {
                    totalPaginas = response.getTotalPages();
                    btnAnterior.setDisable(paginaActual == 0);
                    btnSiguiente.setDisable(paginaActual >= totalPaginas - 1);

                    long total = response.getTotalElements();
                    int paginas = response.getTotalPages();
                    int pageNum = response.getNumber();
                    int pageSize = response.getSize();

                    if (total == 0) {
                        lblResumenPaginacion.setText("No hay clientes para mostrar");
                    } else {
                        long desde = (long) pageNum * pageSize + 1;
                        long hasta = Math.min(desde + customers.size() - 1, total);
                        lblResumenPaginacion.setText("Mostrando " + desde + "-" + hasta + " de " + total + " clientes (Página " + (pageNum + 1) + " de " + paginas + ")");
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
        obtenerClientes();
    }

    @FXML
    private void handleLimpiar() {
        txtBuscar.clear();
        paginaActual = 0;
        obtenerClientes();
    }

    @FXML
    private void handlePaginaAnterior() {
        if (paginaActual > 0) {
            paginaActual--;
            obtenerClientes();
        }
    }

    @FXML
    private void handlePaginaSiguiente() {
        if (paginaActual < totalPaginas - 1) {
            paginaActual++;
            obtenerClientes();
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
        Customer selected = tblClientes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atención");
            alert.setHeaderText(null);
            alert.setContentText("Debe seleccionar un cliente de la lista.");
            com.store.inventario.utils.WindowUtils.applyIcon(alert);
            alert.showAndWait();
            return;
        }
        this.customerSeleccionado = selected;
        cerrarModal();
    }

    @FXML
    private void handleCancelar() {
        this.customerSeleccionado = null;
        cerrarModal();
    }

    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    public Customer getClienteSeleccionado() {
        return customerSeleccionado;
    }
}
