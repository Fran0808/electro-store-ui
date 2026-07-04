package com.store.inventario.module.auth.controller;
 
import com.store.inventario.module.auth.model.entity.User;
import com.store.inventario.module.person.model.enums.EmployeePosition;
import com.store.inventario.service.usuario.UsuarioService;
import com.store.inventario.module.person.service.EmployeeService;
import com.store.inventario.module.person.model.entity.Employee;
import com.store.inventario.model.PageResponse;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
 
import java.net.URL;
import java.util.ResourceBundle;
 
public class UserFormController implements Initializable {
 
    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtRol;
    @FXML
    private ComboBox<String> cbEmpleado;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnGuardar;
 
    private final UsuarioService usuarioService = new UsuarioService();
    private User userEditar;
    private java.util.List<Employee> listaEmployees = new java.util.ArrayList<>();
    private java.util.Set<String> empleadosConUsuario = new java.util.HashSet<>();
 
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarEmpleados();
        
        filtrarYMostrarEmpleados(true);

        cbEmpleado.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String employeeCode = newValue.split(" - ")[0];
                Employee seleccionado = listaEmployees.stream()
                        .filter(e -> e.getCode().equals(employeeCode))
                        .findFirst()
                        .orElse(null);
                if (seleccionado != null) {
                    txtRol.setText(getSystemRoleFromPosition(seleccionado.getPosition()));
                }
            } else {
                txtRol.setText("");
            }
        });
    }

    private String getSystemRoleFromPosition(EmployeePosition position) {
        if (position == null) return "";
        switch (position) {
            case MANAGER: return "ADMIN";
            case SELLER: return "VENDEDOR";
            case STOREKEEPER: return "ALMACENERO";
            default: return "";
        }
    }
 
    private void cargarEmpleados() {
        try {
            PageResponse<User> usuariosResponse = usuarioService.obtenerUsuarios();
            empleadosConUsuario.clear();
            if (usuariosResponse != null && usuariosResponse.getContent() != null) {
                for (User u : usuariosResponse.getContent()) {
                    if (u.getEmployeeCode() != null) {
                        empleadosConUsuario.add(u.getEmployeeCode());
                    }
                }
            }

            EmployeeService employeeService = new EmployeeService();
            PageResponse<Employee> response = employeeService.obtenerEmpleados();
            if (response != null && response.getContent() != null) {
                this.listaEmployees = response.getContent();
            } else {
                this.listaEmployees = new java.util.ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.listaEmployees = new java.util.ArrayList<>();
            this.empleadosConUsuario.clear();
            mostrarAlerta("Error al cargar empleados", "No se pudieron obtener los empleados registrados desde el servidor: " + e.getMessage());
        }
    }

    private void filtrarYMostrarEmpleados(boolean soloSinUsuario) {
        javafx.collections.ObservableList<String> items = FXCollections.observableArrayList();
        for (Employee emp : this.listaEmployees) {
            if (soloSinUsuario && empleadosConUsuario.contains(emp.getCode())) {
                continue;
            }

            String nombre = "";
            if (emp.getPerson() != null) {
                String fn = emp.getPerson().getFirstName() != null ? emp.getPerson().getFirstName() : "";
                String ln = emp.getPerson().getLastName() != null ? emp.getPerson().getLastName() : "";
                nombre = (fn + " " + ln).trim();
            }
            items.add(emp.getCode() + " - " + nombre);
        }
        cbEmpleado.setItems(items);
    }
 
    public void setUsuarioEditar(User user) {
        this.userEditar = user;
        if (lblTitulo != null) {
            lblTitulo.setText("Editar Usuario");
        }
        txtUsername.setText(user.getUsername());
        txtUsername.setEditable(false);
        
        String rolEspanol = user.getRole();
        if ("SELLER".equalsIgnoreCase(rolEspanol)) rolEspanol = "VENDEDOR";
        else if ("STOREKEEPER".equalsIgnoreCase(rolEspanol)) rolEspanol = "ALMACENERO";
        else if (rolEspanol != null) rolEspanol = rolEspanol.toUpperCase().trim();
        txtRol.setText(rolEspanol);
        
        filtrarYMostrarEmpleados(false);
        Employee matchingEmp = null;
        if (user.getEmployeeCode() != null) {
            matchingEmp = listaEmployees.stream()
                    .filter(e -> user.getEmployeeCode().equals(e.getCode()))
                    .findFirst()
                    .orElse(null);
        }
        String empValue;
        if (matchingEmp != null) {
            String nombre = "";
            if (matchingEmp.getPerson() != null) {
                String fn = matchingEmp.getPerson().getFirstName() != null ? matchingEmp.getPerson().getFirstName() : "";
                String ln = matchingEmp.getPerson().getLastName() != null ? matchingEmp.getPerson().getLastName() : "";
                nombre = (fn + " " + ln).trim();
            }
            empValue = matchingEmp.getCode() + " - " + nombre;
        } else {
            empValue = user.getEmployeeCode() + " - " + user.getFullName();
        }
        cbEmpleado.setValue(empValue);
        cbEmpleado.setDisable(true);
 
        txtPassword.setPromptText("Opcional (Dejar vacío para no cambiar)");
    }

    @FXML
    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void guardarUsuario() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String rol = txtRol.getText();
        String empleadoSeleccionado = cbEmpleado.getValue();
 
        boolean esNuevo = (userEditar == null);
        
        if (username == null || username.trim().isEmpty() ||
            (esNuevo && (password == null || password.trim().isEmpty())) ||
            rol == null || rol.trim().isEmpty() || empleadoSeleccionado == null) {
 
            mostrarAlerta("Campos Requeridos", "Por favor, complete todos los campos obligatorios.");
            return;
        }

        try {
            String employeeCode = empleadoSeleccionado.split(" - ")[0];
            
            String rolBackend = rol;
            if ("VENDEDOR".equals(rol)) rolBackend = "SELLER";
            else if ("ALMACENERO".equals(rol)) rolBackend = "STOREKEEPER";
 
            User nuevoUser = new User(
                esNuevo ? null : userEditar.getCode(),
                username.trim(),
                (password != null && !password.trim().isEmpty()) ? password.trim() : null,
                rolBackend,
                employeeCode,
                "",
                ""
            );

            if (esNuevo) {
                usuarioService.crearUsuario(nuevoUser);
            } else {
                usuarioService.actualizarUsuario(userEditar.getCode(), nuevoUser);
            }

            cerrarModal();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al Guardar", "Ocurrió un error al intentar guardar el usuario: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        com.store.inventario.utils.WindowUtils.applyIcon(alerta);
        alerta.showAndWait();
    }
}
