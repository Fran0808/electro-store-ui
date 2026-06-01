package com.store.inventario.controller.clientes;

import com.store.inventario.controller.clientes.ClienteFormController;
import com.store.inventario.model.PageResponse;
import com.store.inventario.model.clientes.Cliente;
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
    private ComboBox<String> cbTipoCliente;
    
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

        if (cbTipoCliente != null) {
            cbTipoCliente.setItems(FXCollections.observableArrayList("Todos", "Personas (DNI)", "Empresas (RUC)"));
            cbTipoCliente.setValue("Todos");
        }

        configurarColumnaAcciones();
        obtenerClientes();
    }

    @FXML
    private void handleNuevoCliente() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/clientes/cliente-form.fxml"));
        Parent root = loader.load();

        Stage modal = new Stage();
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
    private void handleBuscar() throws IOException {
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
                        menuAcciones.getItems().addAll(itemEditar, itemEliminar);

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
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Eliminar cliente?");
        confirmacion.setContentText("Se eliminará al cliente \"" + 
                (cliente.getPerson() != null ? cliente.getPerson().getFirstName() + " " + cliente.getPerson().getLastName() : "") + 
                "\" (Código: " + cliente.getCode() + "). Esta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                clienteService.eliminarCliente(cliente.getCode());

                Alert exito = new Alert(Alert.AlertType.INFORMATION);
                exito.setTitle("Éxito");
                exito.setHeaderText("Cliente Eliminado");
                exito.setContentText("El cliente se ha eliminado correctamente del sistema.");
                exito.showAndWait();

                obtenerClientes();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void obtenerClientes() {
        try {
            PageResponse<Cliente> response = clienteService.obtenerClientes();
            List<Cliente> clientes = response.getContent();
            
            tblClientes.setItems(FXCollections.observableArrayList(clientes));

            if (lblTotalClientes != null) {
                lblTotalClientes.setText(String.valueOf(response.getTotalElements()));
            }
            if (lblClientesDni != null) {
                long totalDni = clientes.stream()
                        .filter(c -> c.getPerson() != null && c.getPerson().getNationalId() != null && !c.getPerson().getNationalId().isEmpty())
                        .count();
                lblClientesDni.setText(String.valueOf(totalDni));
            }
            if (lblClientesRuc != null) {
                long totalRuc = clientes.stream()
                        .filter(c -> c.getTaxId() != null && !c.getTaxId().isEmpty())
                        .count();
                lblClientesRuc.setText(String.valueOf(totalRuc));
            }

            if (lblResumenPaginacion != null) {
                long total = response.getTotalElements();
                int paginas = response.getTotalPages();
                int paginaActual = response.getNumber();
                int pageSize = response.getSize();
                
                if (total == 0) {
                    lblResumenPaginacion.setText("No hay clientes para mostrar");
                } else {
                    long desde = (long) paginaActual * pageSize + 1;
                    long hasta = Math.min(desde + pageSize - 1, total);
                    lblResumenPaginacion.setText("Mostrando " + desde + "-" + hasta + " de " + total + " clientes");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
