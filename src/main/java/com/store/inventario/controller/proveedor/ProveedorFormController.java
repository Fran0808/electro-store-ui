package com.store.inventario.controller.proveedor;

import com.store.inventario.model.proveedor.CreateProveedorRequest;
import com.store.inventario.model.proveedor.Proveedor;
import com.store.inventario.model.proveedor.UpdateProveedorRequest;
import com.store.inventario.service.proveedor.ProveedorService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ProveedorFormController {
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblSubtitulo;
    @FXML
    private TextField txtNombreComercial;
    @FXML
    private TextField txtRazonSocial;
    @FXML
    private TextField txtRuc;
    @FXML
    private TextField txtTelefono;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    private final ProveedorService proveedorService = new ProveedorService();
    private Proveedor proveedorEditar;
    private boolean modoEdicion = false;

    @FXML
    public void initialize() {
        com.store.inventario.utils.ValidationUtils.hacerSoloNumericoConLimite(txtRuc, 11);
        com.store.inventario.utils.ValidationUtils.hacerSoloTelefono(txtTelefono);
    }

    public void setProveedorEditar(Proveedor proveedor) {
        this.proveedorEditar = proveedor;
        this.modoEdicion = true;
        lblTitulo.setText("Editar Proveedor");
        lblSubtitulo.setText("Modifique la información del proveedor");
        btnGuardar.setText("Actualizar Proveedor");
        txtNombreComercial.setText(proveedor.getTradeName());
        txtRazonSocial.setText(proveedor.getLegalName());
        txtRuc.setText(proveedor.getTaxId());
        txtTelefono.setText(proveedor.getPhone());
    }

    @FXML
    private void handleGuardar() {
        if (txtNombreComercial.getText().trim().isEmpty()
                || txtRazonSocial.getText().trim().isEmpty()
                || txtRuc.getText().trim().isEmpty()
                || txtTelefono.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Campos incompletos");
            alert.setContentText("Complete todos los campos obligatorios.");
            alert.showAndWait();
            return;
        }
        try {
            if (modoEdicion) {
                UpdateProveedorRequest updateRequest =
                        new UpdateProveedorRequest(
                                txtRuc.getText().trim(),
                                txtNombreComercial.getText().trim(),
                                txtTelefono.getText().trim(),
                                txtRazonSocial.getText().trim()
                        );
                proveedorService.actualizar(proveedorEditar.getCode(), updateRequest);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText("Proveedor actualizado");
                alert.setContentText("El proveedor se actualizó correctamente.");
                alert.showAndWait();
            } else {
                CreateProveedorRequest createRequest =
                        new CreateProveedorRequest(
                                txtRuc.getText().trim(),
                                txtNombreComercial.getText().trim(),
                                txtTelefono.getText().trim(),
                                txtRazonSocial.getText().trim()
                        );
                proveedorService.crear(createRequest);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText("Proveedor registrado");
                alert.setContentText("El proveedor se creó correctamente.");
                alert.showAndWait();
            }

            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(modoEdicion ? "No se pudo actualizar el proveedor" : "No se pudo registrar el proveedor");
            alert.setContentText("Ocurrió un error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
