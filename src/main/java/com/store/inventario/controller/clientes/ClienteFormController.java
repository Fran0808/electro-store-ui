package com.store.inventario.controller.clientes;

import com.store.inventario.model.clientes.Cliente;
import com.store.inventario.model.clientes.CreateClienteRequest;
import com.store.inventario.model.clientes.UpdateClienteRequest;
import com.store.inventario.model.persona.CreatePersonaRequest;
import com.store.inventario.model.persona.UpdatePersonaRequest;
import com.store.inventario.service.clientes.ClienteService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ClienteFormController implements Initializable {

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtRuc;
    @FXML
    private TextField txtNombres;
    @FXML
    private Label lblApellidos;
    @FXML
    private TextField txtApellidos;
    @FXML
    private TextField txtTelefono;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnGuardar;

    private final ClienteService clienteService = new ClienteService();
    private Cliente clienteEditar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        com.store.inventario.utils.ValidationUtils.hacerSoloTelefono(txtTelefono);
        txtTelefono.setText("+51 ");

        txtDni.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            String sanitized = newValue.replaceAll("[^\\d]", "");
            if (sanitized.length() > 8) {
                sanitized = sanitized.substring(0, 8);
            }
            if (!newValue.equals(sanitized)) {
                txtDni.setText(sanitized);
            }
        });

        txtRuc.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            String sanitized = newValue.replaceAll("[^\\d]", "");
            if (sanitized.length() > 11) {
                sanitized = sanitized.substring(0, 11);
            }
            if (!newValue.equals(sanitized)) {
                txtRuc.setText(sanitized);
            }
        });
    }

    public void setClienteEditar(Cliente cliente) {
        this.clienteEditar = cliente;
        lblTitulo.setText("Editar Cliente");

        if (cliente.getPerson() != null) {
            txtNombres.setText(cliente.getPerson().getFirstName());
            txtApellidos.setText(cliente.getPerson().getLastName() != null ? cliente.getPerson().getLastName() : "");
            txtTelefono.setText(cliente.getPerson().getPhone());
            txtDni.setText(cliente.getPerson().getNationalId() != null ? cliente.getPerson().getNationalId() : "");
            txtRuc.setText(cliente.getTaxId() != null ? cliente.getTaxId() : "");
        }
    }

    @FXML
    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void guardarCliente() {
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String dni = txtDni.getText().trim();
        String ruc = txtRuc.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (nombres.isEmpty()) {
            mostrarAlerta("Campos Requeridos", "El nombre o razón social es obligatorio.");
            return;
        }

        if ("Consumidor".equalsIgnoreCase(nombres) && "Final".equalsIgnoreCase(apellidos)) {
            mostrarAlerta("Nombre Reservado", "No se permite registrar manualmente un cliente con el nombre 'Consumidor Final'.");
            return;
        }

        if (!dni.isEmpty() && dni.length() != 8) {
            mostrarAlerta("Formato Inválido", "El DNI debe tener exactamente 8 dígitos.");
            return;
        }

        if (!ruc.isEmpty() && ruc.length() != 11) {
            mostrarAlerta("Formato Inválido", "El RUC debe tener exactamente 11 dígitos.");
            return;
        }

        String finalNationalId = dni.isEmpty() ? null : dni;
        String finalTaxId = ruc.isEmpty() ? null : ruc;
        String finalApellidos = apellidos.isEmpty() ? null : apellidos;

        try {
            if (clienteEditar == null) {
                CreatePersonaRequest personRequest = new CreatePersonaRequest(
                        nombres, 
                        finalApellidos, 
                        finalNationalId, 
                        telefono
                );
                
                CreateClienteRequest createRequest = new CreateClienteRequest(
                        personRequest,
                        finalTaxId
                );
                
                clienteService.crearCliente(createRequest);
                
            } else {
                UpdatePersonaRequest personRequest = new UpdatePersonaRequest(
                        nombres,
                        finalApellidos,
                        finalNationalId,
                        telefono
                );
                
                UpdateClienteRequest updateRequest = new UpdateClienteRequest(
                        personRequest,
                        finalTaxId
                );
                
                clienteService.actualizarCliente(clienteEditar.getCode(), updateRequest);
            }

            cerrarModal();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al Guardar", "Ocurrió un error al intentar guardar el cliente: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
