package com.store.inventario.module.person.controller;

import com.store.inventario.module.person.request.CreateEmployeeRequest;
import com.store.inventario.module.person.model.entity.Employee;
import com.store.inventario.module.person.model.enums.EmployeePosition;
import com.store.inventario.module.person.request.UpdateEmployeeRequest;
import com.store.inventario.module.person.request.CreatePersonRequest;
import com.store.inventario.module.person.request.UpdatePersonRequest;
import com.store.inventario.module.person.service.EmployeeService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class EmployeeFormController {

    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblSubtitulo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtDni;
    @FXML
    private ComboBox<EmployeePosition> cbCargo;
    @FXML
    private TextField txtSueldo;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    private final EmployeeService employeeService = new EmployeeService();

    private Employee employeeEditar;
    private boolean modoEdicion = false;

    @FXML
    public void initialize() {
        cbCargo.setItems(FXCollections.observableArrayList(EmployeePosition.values()));
        cbCargo.setConverter(new javafx.util.StringConverter<EmployeePosition>() {
            @Override
            public String toString(EmployeePosition position) {
                if (position == null) return "";
                switch (position) {
                    case MANAGER: return "ADMINISTRADOR";
                    case SELLER: return "VENDEDOR";
                    case STOREKEEPER: return "ALMACENERO";
                    default: return position.name();
                }
            }
            @Override
            public EmployeePosition fromString(String string) {
                return null;
            }
        });
        com.store.inventario.utils.ValidationUtils.hacerSoloNumericoConLimite(txtDni, 8);
        com.store.inventario.utils.ValidationUtils.hacerSoloTelefono(txtTelefono);
        txtTelefono.setText("+51 ");
        com.store.inventario.utils.ValidationUtils.hacerSoloDecimal(txtSueldo, 8, 2);
    }

    public void setEmpleadoEditar(Employee employee) {
        this.employeeEditar = employee;
        this.modoEdicion = true;
        lblTitulo.setText("Editar Empleado");
        lblSubtitulo.setText("Modifique la información del empleado");
        btnGuardar.setText("Actualizar");
        txtNombre.setText(employee.getPerson().getFirstName());
        txtApellido.setText(employee.getPerson().getLastName());
        txtTelefono.setText(employee.getPerson().getPhone());
        txtDni.setText(employee.getPerson().getNationalId());
        cbCargo.setValue(employee.getPosition());
        txtSueldo.setText(employee.getSalary() != null ? employee.getSalary().toString() : "");
    }

    @FXML
    private void guardarEmpleado() {
        if (txtNombre.getText().trim().isEmpty() ||
                txtApellido.getText().trim().isEmpty() ||
                txtTelefono.getText().trim().isEmpty() ||
                txtDni.getText().trim().isEmpty() ||
                cbCargo.getValue() == null ||
                txtSueldo.getText().trim().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Campos incompletos");
            alert.setContentText("Complete todos los campos.");
            alert.showAndWait();
            return;
        }

        if (txtDni.getText().trim().length() != 8) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Formato inválido");
            alert.setContentText("El DNI debe tener exactamente 8 dígitos.");
            alert.showAndWait();
            return;
        }

        try {
            BigDecimal sueldo = new BigDecimal(txtSueldo.getText().trim());
            if (modoEdicion) {
                UpdatePersonRequest personRequest = new UpdatePersonRequest(
                        txtNombre.getText().trim(),
                        txtApellido.getText().trim(),
                        txtDni.getText().trim(),
                        txtTelefono.getText().trim());
                UpdateEmployeeRequest updateRequest = new UpdateEmployeeRequest(
                        personRequest,
                        cbCargo.getValue(),
                        sueldo);
                employeeService.actualizarEmpleado(employeeEditar.getCode(), updateRequest);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText("Empleado actualizado");
                alert.setContentText("El empleado se actualizó correctamente.");
                alert.showAndWait();
            } else {
                CreatePersonRequest personRequest = new CreatePersonRequest(
                        txtNombre.getText().trim(),
                        txtApellido.getText().trim(),
                        txtDni.getText().trim(),
                        txtTelefono.getText().trim()
                );
                CreateEmployeeRequest createRequest = new CreateEmployeeRequest(
                        personRequest,
                        cbCargo.getValue(),
                        sueldo
                );
                employeeService.crearEmpleado(createRequest);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText("Empleado registrado");
                alert.setContentText("El empleado se registró correctamente.");
                alert.showAndWait();
            }
            cerrarModal();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Formato inválido");
            alert.setContentText("El sueldo debe ser un número válido.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            if (modoEdicion) {
                alert.setHeaderText("No se pudo actualizar el empleado");
            } else {
                alert.setHeaderText("No se pudo registrar el empleado");
            }
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void cerrarModal(){
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
