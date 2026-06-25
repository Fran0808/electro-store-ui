package com.store.inventario.controller.empleados;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.empleado.Empleado;
import com.store.inventario.service.empleado.EmpleadoService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class EmpleadosController implements Initializable {

    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnBuscar;

    @FXML
    private TableView<Empleado> tblView;
    @FXML
    private TableColumn<Empleado, String> colCodigo;
    @FXML
    private TableColumn<Empleado, String> colNombre;
    @FXML
    private TableColumn<Empleado, String> colApellido;
    @FXML
    private TableColumn<Empleado, String> colTelefono;
    @FXML
    private TableColumn<Empleado, String> colDni;
    @FXML
    private TableColumn<Empleado, String> colCargo;
    @FXML
    private TableColumn<Empleado, BigDecimal> colSueldo;
    @FXML
    private TableColumn<Empleado, Void> colAcciones;

    @FXML
    private Label lblResumenPaginacion;
    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnSiguiente;

    private final EmpleadoService empleadoService = new EmpleadoService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("code"));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData
                .getValue()
                .getPerson()
                .getFirstName()));
        colApellido.setCellValueFactory(cellData -> new SimpleStringProperty(cellData
                .getValue()
                .getPerson()
                .getLastName()));
        colTelefono.setCellValueFactory(cellData -> new SimpleStringProperty(cellData
                .getValue()
                .getPerson()
                .getPhone()));
        colDni.setCellValueFactory(cellData -> new SimpleStringProperty(cellData
                .getValue()
                .getPerson()
                .getNationalId()));
        colCargo.setCellValueFactory(cellData -> {
            var position = cellData.getValue().getPosition();
            String cargoEspanol = "";
            if (position != null) {
                switch (position) {
                    case MANAGER: cargoEspanol = "MANAGER"; break;
                    case SELLER: cargoEspanol = "RECEPCIONISTA"; break;
                    case STOREKEEPER: cargoEspanol = "ALMACENERO"; break;
                    default: cargoEspanol = position.name();
                }
            }
            return new SimpleStringProperty(cargoEspanol);
        });
        colSueldo.setCellValueFactory(new PropertyValueFactory<>("salary"));
        configurarColumnaAcciones();
        obtenerEmpleados();
    }

    private void obtenerEmpleados() {
        try {
            PageResponse<Empleado> response = empleadoService.obtenerEmpleados();
            tblView.setItems(FXCollections.observableArrayList(response.getContent()));
            actualizarPaginacion(response);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudieron obtener los empleados");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void abrirModalNuevoEmpleado() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/empleados/empleado-form.fxml"));
        Parent root = loader.load();
        Stage modal = new Stage();
        com.store.inventario.utils.WindowUtils.applyIcon(modal);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Nuevo Empleado");
        modal.setResizable(false);
        modal.setScene(new Scene(root));
        modal.showAndWait();
        obtenerEmpleados();
    }

    private void handleEditar(Empleado empleado){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/empleados/empleado-form.fxml"));
            Parent root = loader.load();
            EmpleadosFormController controller = loader.getController();
            controller.setEmpleadoEditar(empleado);
            Stage modal = new Stage();
            com.store.inventario.utils.WindowUtils.applyIcon(modal);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Editar Empleado");
            modal.setResizable(false);
            modal.setScene(new Scene(root));
            modal.showAndWait();
            obtenerEmpleados();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo abrir el formulario");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleEliminar(Empleado empleado){
        Platform.runLater(() -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Eliminacion");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("Se eliminara al empleado " + empleado.getPerson().getFirstName()
                    + " "
                    + empleado.getPerson().getLastName()
                    + ".");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    empleadoService.eliminarEmpleado(empleado.getCode());
                    
                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Exito");
                    exito.setHeaderText("Empleado eliminado");
                    exito.setContentText("El empleado se elimino correctamente.");
                    exito.showAndWait();
                    
                    obtenerEmpleados();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Error");
                    error.setHeaderText("No se pudo eliminar el empleado");
                    error.setContentText("No se pudo eliminar al empleado. El empleado tiene un usuario asociado en el sistema o registros dependientes.");
                    error.showAndWait();
                }
            }
        });
    }

    private void configurarColumnaAcciones(){
        Callback<TableColumn<Empleado, Void>, TableCell<Empleado, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Empleado, Void> call(final TableColumn<Empleado, Void> param) {
                return new TableCell<>(){
                    private final Button btnAcciones = new Button("⋮");
                    private final ContextMenu menuAcciones = new ContextMenu();
                    private final MenuItem itemEditar = new MenuItem("Editar");
                    private final MenuItem itemEliminar = new MenuItem("Eliminar");
                    private final HBox contenedor = new HBox(btnAcciones); {
                        btnAcciones.getStyleClass().add("btn-acciones");
                        btnAcciones.setTooltip(new Tooltip("Acciones del empleado"));
                        menuAcciones.getItems().addAll(itemEditar, itemEliminar);
                        contenedor.setAlignment(Pos.CENTER);
                        btnAcciones.setOnAction(event -> menuAcciones.show(
                                btnAcciones,
                                javafx.geometry.Side.BOTTOM,
                                0,
                                0));
                        itemEditar.setOnAction(event -> {
                            Empleado empleado = getTableView().getItems().get(getIndex());
                            handleEditar(empleado);
                        });
                        itemEliminar.setOnAction(event -> {
                            Empleado empleado = getTableView().getItems().get(getIndex());
                            handleEliminar(empleado);
                        });
                    }
                    @Override
                    protected void updateItem(Void item, boolean empty){
                        super.updateItem(item, empty);
                        if(empty) {
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

    private void actualizarPaginacion(PageResponse<Empleado> response){
        long total = response.getTotalElements();
        int paginaActual = response.getNumber();
        int totalPaginas = response.getTotalPages();
        int pageSize = response.getSize();
        if (total == 0) {
            lblResumenPaginacion.setText("No hay empleados para mostrar");
            return;
        }
        long desde = (long) paginaActual * pageSize + 1;
        long hasta = Math.min(desde + pageSize - 1, total);
        lblResumenPaginacion.setText("Mostrando "
                        + desde
                        + "-"
                        + hasta
                        + " de "
                        + total
                        + " empleados (Página "
                        + (paginaActual + 1)
                        + " de "
                        + totalPaginas
                        + ")"
        );
    }
}