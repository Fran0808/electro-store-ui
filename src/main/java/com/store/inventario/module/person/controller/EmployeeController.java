package com.store.inventario.module.person.controller;

import com.store.inventario.model.PageResponse;
import com.store.inventario.module.person.model.entity.Employee;
import com.store.inventario.module.person.service.EmployeeService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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

public class EmployeeController implements Initializable {

    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnBuscar;

    @FXML
    private TableView<Employee> tblView;
    @FXML
    private TableColumn<Employee, String> colCodigo;
    @FXML
    private TableColumn<Employee, String> colNombre;
    @FXML
    private TableColumn<Employee, String> colApellido;
    @FXML
    private TableColumn<Employee, String> colTelefono;
    @FXML
    private TableColumn<Employee, String> colDni;
    @FXML
    private TableColumn<Employee, String> colCargo;
    @FXML
    private TableColumn<Employee, BigDecimal> colSueldo;
    @FXML
    private TableColumn<Employee, Void> colAcciones;

    @FXML
    private Label lblResumenPaginacion;
    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnSiguiente;

    private final EmployeeService employeeService = new EmployeeService();
    private final ObservableList<Employee> masterData = FXCollections.observableArrayList();
    private FilteredList<Employee> filteredData;
    private int paginaActual = 0;
    private final int tamanoPagina = 30;
    private int totalPaginas = 1;

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
                    case MANAGER: cargoEspanol = "ADMINISTRADOR"; break;
                    case SELLER: cargoEspanol = "VENDEDOR"; break;
                    case STOREKEEPER: cargoEspanol = "ALMACENERO"; break;
                    default: cargoEspanol = position.name();
                }
            }
            return new SimpleStringProperty(cargoEspanol);
        });
        colSueldo.setCellValueFactory(new PropertyValueFactory<>("salary"));
        configurarColumnaAcciones();
        
        filteredData = new FilteredList<>(masterData, p -> true);
        tblView.setItems(filteredData);
        
        btnAnterior.setOnAction(e -> handlePaginaAnterior());
        btnSiguiente.setOnAction(e -> handlePaginaSiguiente());

        obtenerEmpleados();
    }

    private void obtenerEmpleados() {
        try {
            PageResponse<Employee> response = employeeService.obtenerEmpleados(paginaActual, tamanoPagina);
            masterData.setAll(response.getContent());
            totalPaginas = response != null ? response.getTotalPages() : 1;
            btnAnterior.setDisable(paginaActual == 0);
            btnSiguiente.setDisable(paginaActual >= totalPaginas - 1);
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/person/employee-form.fxml"));
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

    private void handleEditar(Employee employee){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/store/inventario/views/person/employee-form.fxml"));
            Parent root = loader.load();
            EmployeeFormController controller = loader.getController();
            controller.setEmpleadoEditar(employee);
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

    private void handleEliminar(Employee employee){
        Platform.runLater(() -> {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Eliminacion");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("Se eliminara al empleado " + employee.getPerson().getFirstName()
                    + " "
                    + employee.getPerson().getLastName()
                    + ".");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    employeeService.eliminarEmpleado(employee.getCode());
                    
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
        Callback<TableColumn<Employee, Void>, TableCell<Employee, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Employee, Void> call(final TableColumn<Employee, Void> param) {
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
                            Employee employee = getTableView().getItems().get(getIndex());
                            handleEditar(employee);
                        });
                        itemEliminar.setOnAction(event -> {
                            Employee employee = getTableView().getItems().get(getIndex());
                            handleEliminar(employee);
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

    private void actualizarPaginacion(PageResponse<Employee> response){
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

    @FXML
    private void ejecutarBusqueda() {
        String text = txtBuscar.getText();
        if (text == null || text.trim().isEmpty()) {
            filteredData.setPredicate(p -> true);
        } else {
            String lowerCaseFilter = text.toLowerCase().trim();
            filteredData.setPredicate(employee -> {
                if (employee.getCode() != null && employee.getCode().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                if (employee.getPerson() != null) {
                    var person = employee.getPerson();
                    if (person.getFirstName() != null && person.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    if (person.getLastName() != null && person.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    if (person.getNationalId() != null && person.getNationalId().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                }
                if (employee.getPosition() != null && employee.getPosition().name().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        }
        actualizarPaginacionConFiltrados();
    }

    @FXML
    private void limpiarBusqueda() {
        txtBuscar.clear();
        filteredData.setPredicate(p -> true);
        actualizarPaginacionConFiltrados();
    }

    private void actualizarPaginacionConFiltrados() {
        int total = filteredData.size();
        if (total == 0) {
            lblResumenPaginacion.setText("No hay empleados para mostrar");
            return;
        }
        lblResumenPaginacion.setText("Mostrando 1-" + total + " de " + total + " empleados");
    }

    private void handlePaginaAnterior() {
        if (paginaActual > 0) {
            paginaActual--;
            obtenerEmpleados();
        }
    }

    private void handlePaginaSiguiente() {
        if (paginaActual < totalPaginas - 1) {
            paginaActual++;
            obtenerEmpleados();
        }
    }
}