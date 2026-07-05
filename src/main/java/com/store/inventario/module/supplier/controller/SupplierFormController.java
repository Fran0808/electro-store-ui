package com.store.inventario.module.supplier.controller;

import com.store.inventario.module.supplier.request.CreateSupplierRequest;
import com.store.inventario.module.supplier.model.entity.Supplier;
import com.store.inventario.module.supplier.request.UpdateSupplierRequest;
import com.store.inventario.module.supplier.service.SupplierService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SupplierFormController {
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

    private final SupplierService supplierService = new SupplierService();
    private Supplier supplierEditar;
    private boolean modoEdicion = false;

    @FXML
    public void initialize() {
        com.store.inventario.shared.utils.ValidationUtils.hacerSoloNumericoConLimite(txtRuc, 11);
        com.store.inventario.shared.utils.ValidationUtils.hacerSoloTelefono(txtTelefono);
        txtTelefono.setText("+51 ");
    }

    public void setProveedorEditar(Supplier supplier) {
        this.supplierEditar = supplier;
        this.modoEdicion = true;
        lblTitulo.setText("Editar Proveedor");
        lblSubtitulo.setText("Modifique la información del proveedor");
        btnGuardar.setText("Actualizar Proveedor");
        txtNombreComercial.setText(supplier.getTradeName());
        txtRazonSocial.setText(supplier.getLegalName());
        txtRuc.setText(supplier.getTaxId());
        txtTelefono.setText(supplier.getPhone());
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
                UpdateSupplierRequest updateRequest =
                        new UpdateSupplierRequest(
                                txtRuc.getText().trim(),
                                txtNombreComercial.getText().trim(),
                                txtTelefono.getText().trim(),
                                txtRazonSocial.getText().trim()
                        );
                supplierService.actualizar(supplierEditar.getCode(), updateRequest);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText("Proveedor actualizado");
                alert.setContentText("El proveedor se actualizó correctamente.");
                alert.showAndWait();
            } else {
                CreateSupplierRequest createRequest =
                        new CreateSupplierRequest(
                                txtRuc.getText().trim(),
                                txtNombreComercial.getText().trim(),
                                txtTelefono.getText().trim(),
                                txtRazonSocial.getText().trim()
                        );
                supplierService.crear(createRequest);
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
