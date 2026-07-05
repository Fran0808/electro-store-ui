package com.store.inventario.component;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

public class ModalController {

    @FXML
    private StackPane paneIcono;
    @FXML
    private SVGPath svgAlerta;
    @FXML
    private SVGPath svgError;
    @FXML
    private Label lblCategoria;
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblMensaje;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnConfirmar;

    @FXML
    private void initialize() {}

    public void setTitulo(String titulo) {
        this.lblTitulo.setText(titulo);
    }

    public void setMensaje(String mensaje) {
        this.lblMensaje.setText(mensaje);
    }

    public void setCategoria(String categoria) {
        this.lblCategoria.setText(categoria);
    }

    public void setIcono(String icono) {
        svgAlerta.setVisible(false);
        svgAlerta.setManaged(false);
        svgError.setVisible(false);
        svgError.setManaged(false);

        paneIcono.getStyleClass().clear();
        paneIcono.getStyleClass().addAll("metric-icon-container", "icon-blue");

        if ("ADVERTENCIA".equals(icono)) {
            svgAlerta.setVisible(true);
            svgAlerta.setManaged(true);
        } else if ("ERROR".equals(icono)) {
            svgError.setVisible(true);
            svgError.setManaged(true);
        }
    }

    public Button getBtnConfirmar() {
        return btnConfirmar;
    }

    public Button getBtnCancelar() {
        return btnCancelar;
    }

    public void mostrarBotonCancelar(boolean mostrar) {
        btnCancelar.setVisible(mostrar);
        btnCancelar.setManaged(mostrar);
    }

    public void mostrarBotonConfirmar(boolean mostrar) {
        btnConfirmar.setVisible(mostrar);
        btnConfirmar.setManaged(mostrar);
    }
}