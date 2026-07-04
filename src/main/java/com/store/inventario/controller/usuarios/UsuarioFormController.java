package com.store.inventario.controller.usuarios;
 
import com.store.inventario.model.usuario.Usuario;
import com.store.inventario.service.usuario.UsuarioService;
import com.store.inventario.service.empleado.EmpleadoService;
import com.store.inventario.model.empleado.Empleado;
import com.store.inventario.model.PageResponse;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
 
import java.net.URL;
import java.util.ResourceBundle;
 
public class UsuarioFormController implements Initializable {
 
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
    private Usuario usuarioEditar;
    private java.util.List<Empleado> listaEmpleados = new java.util.ArrayList<>();
    private java.util.Set<String> empleadosConUsuario = new java.util.HashSet<>();
 
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarEmpleados();
        
        filtrarYMostrarEmpleados(true);

        cbEmpleado.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String employeeCode = newValue.split(" - ")[0];
                Empleado seleccionado = listaEmpleados.stream()
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

    private String getSystemRoleFromPosition(com.store.inventario.model.empleado.EmployeePosition position) {
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
            PageResponse<Usuario> usuariosResponse = usuarioService.obtenerUsuarios();
            empleadosConUsuario.clear();
            if (usuariosResponse != null && usuariosResponse.getContent() != null) {
                for (Usuario u : usuariosResponse.getContent()) {
                    if (u.getEmployeeCode() != null) {
                        empleadosConUsuario.add(u.getEmployeeCode());
                    }
                }
            }

            EmpleadoService empleadoService = new EmpleadoService();
            PageResponse<Empleado> response = empleadoService.obtenerEmpleados();
            if (response != null && response.getContent() != null) {
                this.listaEmpleados = response.getContent();
            } else {
                this.listaEmpleados = new java.util.ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.listaEmpleados = new java.util.ArrayList<>();
            this.empleadosConUsuario.clear();
            mostrarAlerta("Error al cargar empleados", "No se pudieron obtener los empleados registrados desde el servidor: " + e.getMessage());
        }
    }

    private void filtrarYMostrarEmpleados(boolean soloSinUsuario) {
        javafx.collections.ObservableList<String> items = FXCollections.observableArrayList();
        for (Empleado emp : this.listaEmpleados) {
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
 
    public void setUsuarioEditar(Usuario usuario) {
        this.usuarioEditar = usuario;
        if (lblTitulo != null) {
            lblTitulo.setText("Editar Usuario");
        }
        txtUsername.setText(usuario.getUsername());
        txtUsername.setEditable(false);
        
        String rolEspanol = usuario.getRole();
        if ("SELLER".equalsIgnoreCase(rolEspanol)) rolEspanol = "VENDEDOR";
        else if ("STOREKEEPER".equalsIgnoreCase(rolEspanol)) rolEspanol = "ALMACENERO";
        else if (rolEspanol != null) rolEspanol = rolEspanol.toUpperCase().trim();
        txtRol.setText(rolEspanol);
        
        filtrarYMostrarEmpleados(false);
        Empleado matchingEmp = null;
        if (usuario.getEmployeeCode() != null) {
            matchingEmp = listaEmpleados.stream()
                    .filter(e -> usuario.getEmployeeCode().equals(e.getCode()))
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
            empValue = usuario.getEmployeeCode() + " - " + usuario.getFullName();
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
 
        boolean esNuevo = (usuarioEditar == null);
        
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
 
            Usuario nuevoUsuario = new Usuario(
                esNuevo ? null : usuarioEditar.getCode(),
                username.trim(),
                (password != null && !password.trim().isEmpty()) ? password.trim() : null,
                rolBackend,
                employeeCode,
                "",
                ""
            );

            if (esNuevo) {
                usuarioService.crearUsuario(nuevoUsuario);
            } else {
                usuarioService.actualizarUsuario(usuarioEditar.getCode(), nuevoUsuario);
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
