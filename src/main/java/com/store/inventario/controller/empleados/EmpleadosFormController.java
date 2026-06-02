package com.store.inventario.controller.empleados;

import com.store.inventario.model.empleado.CreateEmployeeRequest;
import com.store.inventario.model.empleado.Empleado;
import com.store.inventario.model.empleado.EmployeePosition;
import com.store.inventario.model.empleado.UpdateEmployeeRequest;
import com.store.inventario.model.persona.CreatePersonaRequest;
import com.store.inventario.model.persona.UpdatePersonaRequest;
import com.store.inventario.service.empleado.EmpleadoService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class EmpleadosFormController {

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

    private final EmpleadoService empleadoService = new EmpleadoService();

    private Empleado empleadoEditar;
    private boolean modoEdicion = false;

    @FXML
    public void initialize() {
        cbCargo.setItems(FXCollections.observableArrayList(EmployeePosition.values()));
        cbCargo.setConverter(new javafx.util.StringConverter<EmployeePosition>() {
            @Override
            public String toString(EmployeePosition position) {
                if (position == null) return "";
                switch (position) {
                    case MANAGER: return "MANAGER";
                    case SELLER: return "RECEPCIONISTA";
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
        com.store.inventario.utils.ValidationUtils.hacerSoloDecimal(txtSueldo);
    }

    public void setEmpleadoEditar(Empleado empleado) {
        this.empleadoEditar = empleado;
        this.modoEdicion = true;
        lblTitulo.setText("Editar Empleado");
        lblSubtitulo.setText("Modifique la información del empleado");
        btnGuardar.setText("Actualizar");
        txtNombre.setText(empleado.getPerson().getFirstName());
        txtApellido.setText(empleado.getPerson().getLastName());
        txtTelefono.setText(empleado.getPerson().getPhone());
        txtDni.setText(empleado.getPerson().getNationalId());
        cbCargo.setValue(empleado.getPosition());
        txtSueldo.setText(empleado.getSalary() != null ? empleado.getSalary().toString() : "");
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
                UpdatePersonaRequest personRequest = new UpdatePersonaRequest(
                        txtNombre.getText().trim(),
                        txtApellido.getText().trim(),
                        txtDni.getText().trim(),
                        txtTelefono.getText().trim());
                UpdateEmployeeRequest updateRequest = new UpdateEmployeeRequest(
                        personRequest,
                        cbCargo.getValue(),
                        sueldo);
                empleadoService.actualizarEmpleado(empleadoEditar.getCode(), updateRequest);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText("Empleado actualizado");
                alert.setContentText("El empleado se actualizó correctamente.");
                alert.showAndWait();
            } else {
                CreatePersonaRequest personRequest = new CreatePersonaRequest(
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
                empleadoService.crearEmpleado(createRequest);
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
