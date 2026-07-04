package com.store.inventario.controller.clientes;

import com.store.inventario.controller.clientes.ClienteFormController;
import com.store.inventario.model.PageResponse;
import com.store.inventario.model.clientes.Cliente;
import com.store.inventario.security.SessionManager;
import com.store.inventario.service.clientes.ClienteService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.fxml.Initializable;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClienteController implements Initializable {
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnNuevoCliente;
    @FXML
    private Button btnBuscar;
    @FXML
    private TextField txtBuscar;

    
    @FXML
    private Label lblTotalClientes;
    @FXML
    private Label lblClientesRuc;
    @FXML
    private Label lblClientesDni;
    
    @FXML
    private Label lblResumenPaginacion;
    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnSiguiente;

    @FXML
    private TableView<Cliente> tblClientes;
    @FXML
    private TableColumn<Cliente, String> colCodigo;
    @FXML
    private TableColumn<Cliente, String> colNombre;
    @FXML
    private TableColumn<Cliente, String> colDni;
    @FXML
    private TableColumn<Cliente, String> colRuc;
    @FXML
    private TableColumn<Cliente, String> colTelefono;
    @FXML
    private TableColumn<Cliente, Void> colAcciones;

    private final ClienteService clienteService = new ClienteService();
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
        
        colTelefono.setCellValueFactory(cellData -> {
            var person = cellData.getValue().getPerson();
            return new SimpleStringProperty(person != null ? person.getPhone() : "");
        });



        if (btnAnterior != null) {
            btnAnterior.setOnAction(e -> handlePaginaAnterior());
        }
        if (btnSiguiente != null) {
            btnSiguiente.setOnAction(e -> handlePaginaSiguiente());
        }

        configurarColumnaAcciones();
        obtenerClientes();
    }

    @FXML
    private void handleNuevoCliente() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/clientes/cliente-form.fxml"));
        Parent root = loader.load();

        Stage modal = new Stage();
        com.store.inventario.utils.WindowUtils.applyIcon(modal);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Nuevo Cliente");
        modal.setScene(new Scene(root));
        modal.showAndWait();

        obtenerClientes();
    }

    @FXML
    private void handleActualizar() throws IOException {
        obtenerClientes();
    }

    @FXML
    private void handleBuscar() {
        paginaActual = 0;
        obtenerClientes();
    }

    @FXML
    private void handleLimpiar() {
        if (txtBuscar != null) {
            txtBuscar.clear();
        }

        paginaActual = 0;
        obtenerClientes();
    }

    private void configurarColumnaAcciones() {
        Callback<TableColumn<Cliente, Void>, TableCell<Cliente, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Cliente, Void> call(final TableColumn<Cliente, Void> param) {
                return new TableCell<>() {
                    private final Button btnAcciones = new Button("⋮");
                    private final ContextMenu menuAcciones = new ContextMenu();
                    private final MenuItem itemEditar = new MenuItem("Editar");
                    private final MenuItem itemEliminar = new MenuItem("Eliminar");
                    private final HBox contenedor = new HBox(btnAcciones);

                    {
                        btnAcciones.getStyleClass().add("btn-acciones");
                        btnAcciones.setTooltip(new Tooltip("Acciones de Cliente"));

                        itemEditar.getStyleClass().add("menu-item-editar");
                        itemEliminar.getStyleClass().add("menu-item-eliminar");

                        String rolActual = SessionManager.getInstance().getRole();

                        if(!"ADMIN".equalsIgnoreCase(rolActual)){
                            menuAcciones.getItems().addAll(itemEditar);
                        }else{
                            menuAcciones.getItems().addAll(itemEditar, itemEliminar);
                        }

                        contenedor.setAlignment(Pos.CENTER);

                        btnAcciones.setOnAction(event -> {
                            menuAcciones.show(btnAcciones, javafx.geometry.Side.BOTTOM, 0, 0);
                        });

                        itemEditar.setOnAction(event -> {
                            Cliente cliente = getTableView().getItems().get(getIndex());
                            handleEditar(cliente);
                        });

                        itemEliminar.setOnAction(event -> {
                            Cliente cliente = getTableView().getItems().get(getIndex());
                            handleEliminar(cliente);
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

    private void handleEditar(Cliente cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/clientes/cliente-form.fxml"));
            Parent root = loader.load();

            ClienteFormController controller = loader.getController();
            controller.setClienteEditar(cliente);

            Stage modal = new Stage();
            com.store.inventario.utils.WindowUtils.applyIcon(modal);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Editar Cliente");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            obtenerClientes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEliminar(Cliente cliente) {
        javafx.application.Platform.runLater(() -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Eliminación");
            confirmacion.setHeaderText("¿Eliminar cliente?");
            confirmacion.setContentText("Se eliminará al cliente \"" + 
                    (cliente.getPerson() != null ? cliente.getPerson().getFirstName() + " " + cliente.getPerson().getLastName() : "") + 
                    "\" (Código: " + cliente.getCode() + "). Esta acción no se puede deshacer.");
            com.store.inventario.utils.WindowUtils.applyIcon(confirmacion);

            Optional<ButtonType> resultado = confirmacion.showAndWait();

            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    clienteService.eliminarCliente(cliente.getCode());

                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Éxito");
                    exito.setHeaderText("Cliente Eliminado");
                    exito.setContentText("El cliente se ha eliminado correctamente del sistema.");
                    com.store.inventario.utils.WindowUtils.applyIcon(exito);
                    exito.showAndWait();

                    obtenerClientes();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("No se pudo eliminar el cliente");
                    error.setContentText("Ocurrió un error al intentar eliminar al cliente. Asegúrese de que no tenga registros dependientes en el sistema: " + e.getMessage());
                    com.store.inventario.utils.WindowUtils.applyIcon(error);
                    error.showAndWait();
                }
            }
        });
    }

    private void obtenerClientes() {
        try {
            String search = (txtBuscar != null) ? txtBuscar.getText().trim() : "";
            PageResponse<Cliente> response = clienteService.obtenerClientes(search, paginaActual, tamanoPagina);
            List<Cliente> clientes = (response != null && response.getContent() != null) 
                    ? response.getContent() 
                    : java.util.Collections.emptyList();
            
            tblClientes.setItems(FXCollections.observableArrayList(clientes));

            if (response != null) {
                totalPaginas = response.getTotalPages();
                if (btnAnterior != null) btnAnterior.setDisable(paginaActual == 0);
                if (btnSiguiente != null) btnSiguiente.setDisable(paginaActual >= totalPaginas - 1);
            }

            try {
                com.store.inventario.model.clientes.CustomerMetrics metrics = clienteService.obtenerMetricas();
                if (metrics != null) {
                    if (lblTotalClientes != null) lblTotalClientes.setText(String.valueOf(metrics.getTotalCustomers()));
                    if (lblClientesDni != null) lblClientesDni.setText(String.valueOf(metrics.getTotalWithDni()));
                    if (lblClientesRuc != null) lblClientesRuc.setText(String.valueOf(metrics.getTotalWithRuc()));
                } else if (response != null) {
                    if (lblTotalClientes != null) lblTotalClientes.setText(String.valueOf(response.getTotalElements()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (lblTotalClientes != null && response != null) {
                    lblTotalClientes.setText(String.valueOf(response.getTotalElements()));
                }
            }

            if (lblResumenPaginacion != null && response != null) {
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
}
