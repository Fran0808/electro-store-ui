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
    private ComboBox<String> cbTipoDocumento;
    @FXML
    private TextField txtNumeroDocumento;
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
        cbTipoDocumento.getItems().addAll("DNI", "RUC");
        cbTipoDocumento.setValue("DNI");

        com.store.inventario.utils.ValidationUtils.hacerSoloTelefono(txtTelefono);

        // Limitar numero de documento dinamicamente segun el tipo seleccionado
        cbTipoDocumento.valueProperty().addListener((observable, oldValue, newValue) -> {
            txtNumeroDocumento.setText(""); // Limpiar para evitar conflicto de longitud
            if ("RUC".equals(newValue)) {
                lblApellidos.setVisible(false);
                txtApellidos.setVisible(false);
                txtApellidos.setText("");
            } else {
                lblApellidos.setVisible(true);
                txtApellidos.setVisible(true);
            }
        });

        // Validar en tiempo real que solo ingresen digitos y respetar el limite
        txtNumeroDocumento.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            String tipoDoc = cbTipoDocumento.getValue();
            int limite = "RUC".equals(tipoDoc) ? 11 : 8;
            String sanitized = newValue.replaceAll("[^\\d]", "");
            if (sanitized.length() > limite) {
                sanitized = sanitized.substring(0, limite);
            }
            if (!newValue.equals(sanitized)) {
                txtNumeroDocumento.setText(sanitized);
            }
        });
    }

    public void setClienteEditar(Cliente cliente) {
        this.clienteEditar = cliente;
        lblTitulo.setText("Editar Cliente");

        if (cliente.getPerson() != null) {
            txtNombres.setText(cliente.getPerson().getFirstName());
            txtApellidos.setText(cliente.getPerson().getLastName());
            txtTelefono.setText(cliente.getPerson().getPhone());

            if (cliente.getTaxId() != null && !cliente.getTaxId().isEmpty() && !cliente.getTaxId().startsWith("DNI-")) {
                cbTipoDocumento.setValue("RUC");
                txtNumeroDocumento.setText(cliente.getTaxId());
            } else {
                cbTipoDocumento.setValue("DNI");
                txtNumeroDocumento.setText(cliente.getPerson().getNationalId());
            }
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
        String numeroDoc = txtNumeroDocumento.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String tipoDoc = cbTipoDocumento.getValue();

        if (nombres.isEmpty()) {
            mostrarAlerta("Campos Requeridos", "El nombre o razón social es obligatorio.");
            return;
        }

        if (numeroDoc.isEmpty()) {
            mostrarAlerta("Campos Requeridos", "El número de documento es obligatorio.");
            return;
        }

        if ("DNI".equals(tipoDoc) && numeroDoc.length() != 8) {
            mostrarAlerta("Formato Inválido", "El DNI debe tener exactamente 8 dígitos.");
            return;
        }

        if ("RUC".equals(tipoDoc) && numeroDoc.length() != 11) {
            mostrarAlerta("Formato Inválido", "El RUC debe tener exactamente 11 dígitos.");
            return;
        }

        if ("DNI".equals(tipoDoc) && apellidos.isEmpty()) {
            mostrarAlerta("Campos Requeridos", "Los apellidos son obligatorios para personas con DNI.");
            return;
        }

        try {
            if (clienteEditar == null) {
                CreatePersonaRequest personRequest = new CreatePersonaRequest(
                        nombres, 
                        "DNI".equals(tipoDoc) ? apellidos : "-", // Si es RUC, se rellena con guion
                        "DNI".equals(tipoDoc) ? numeroDoc : ("RUC-" + numeroDoc), 
                        telefono
                );
                
                CreateClienteRequest createRequest = new CreateClienteRequest(
                        personRequest,
                        "RUC".equals(tipoDoc) ? numeroDoc : ("DNI-" + numeroDoc)
                );
                
                clienteService.crearCliente(createRequest);
                
            } else {
                UpdatePersonaRequest personRequest = new UpdatePersonaRequest(
                        nombres,
                        "DNI".equals(tipoDoc) ? apellidos : "-",
                        "DNI".equals(tipoDoc) ? numeroDoc : ("RUC-" + numeroDoc),
                        telefono
                );
                
                UpdateClienteRequest updateRequest = new UpdateClienteRequest(
                        personRequest,
                        "RUC".equals(tipoDoc) ? numeroDoc : ("DNI-" + numeroDoc)
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
