package com.store.inventario.controller.clientes;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.clientes.Cliente;
import com.store.inventario.service.clientes.ClienteService;
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

public class ClienteSearchModalController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, String> colCodigo;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, String> colRuc;
    @FXML private Label lblResumenPaginacion;
    @FXML private Button btnAnterior;
    @FXML private Button btnSiguiente;
    @FXML private Button btnCancelar;
    @FXML private Button btnSeleccionar;

    private final ClienteService clienteService = new ClienteService();
    private Cliente clienteSeleccionado = null;
    
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
                PageResponse<Cliente> response = clienteService.obtenerClientes(search, paginaActual, tamanoPagina);
                List<Cliente> clientes = (response != null && response.getContent() != null)
                        ? response.getContent()
                        : java.util.Collections.emptyList();

                tblClientes.setItems(FXCollections.observableArrayList(clientes));

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
                        long hasta = Math.min(desde + clientes.size() - 1, total);
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
        Cliente selected = tblClientes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atención");
            alert.setHeaderText(null);
            alert.setContentText("Debe seleccionar un cliente de la lista.");
            alert.showAndWait();
            return;
        }
        this.clienteSeleccionado = selected;
        cerrarModal();
    }

    @FXML
    private void handleCancelar() {
        this.clienteSeleccionado = null;
        cerrarModal();
    }

    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    public Cliente getClienteSeleccionado() {
        return clienteSeleccionado;
    }
}
