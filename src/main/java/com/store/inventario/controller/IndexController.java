package com.store.inventario.controller;

import com.store.inventario.model.NavigationManager;
import com.store.inventario.security.SessionManager;
import com.store.inventario.utils.DialogUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class IndexController implements Initializable {

    @FXML
    private Label lblBienvenida;

    @FXML
    private Label lblFechaHora;

    @FXML
    private TableView<Actividad> tablaActividad;

    @FXML
    private TableColumn<Actividad, String> colHora;

    @FXML
    private TableColumn<Actividad, String> colUsuario;

    @FXML
    private TableColumn<Actividad, String> colAccion;

    @FXML
    private Label lblUltimaCopia;

    @FXML
    private Label lblEstadoCopia;

    @FXML
    private Label lblUbicacionCopia;

    @FXML
    private Button btnRealizarCopia;

    @FXML
    private Button btnQuickVenta;

    @FXML
    private Button btnQuickInventario;

    @FXML
    private Button btnQuickCliente;

    @FXML
    private Button btnQuickGuia;

    @FXML
    private Button btnQuickSoporte;

    @FXML
    private VBox cardBackup;

    @FXML
    private Label lblBackupTitle;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String username = SessionManager.getInstance().getUsername();
        if (username == null || username.trim().isEmpty()) {
            username = "Administrador";
        } else {
            username = username.substring(0, 1).toUpperCase() + username.substring(1);
        }
        lblBienvenida.setText("¡Bienvenido, " + username + "!");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm:ss a");
        lblFechaHora.setText("Fecha y Hora: " + LocalDateTime.now().format(formatter));

        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            lblFechaHora.setText("Fecha y Hora: " + LocalDateTime.now().format(formatter));
        }));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        initTablaActividad();
        configurarSeguridadYVistas();
    }

    private void initTablaActividad() {
        colHora.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getHora()));
        colUsuario.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsuario()));
        colAccion.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAccion()));

        tablaActividad.setItems(FXCollections.observableArrayList());
    }

    private void configurarSeguridadYVistas() {
        String role = SessionManager.getInstance().getRole();
        if (role == null) {
            role = "";
        } else {
            role = role.trim().toUpperCase();
        }

        if ("ADMIN".equals(role)) {
            lblBackupTitle.setText("Copia de Seguridad");
            lblUltimaCopia.setText("Ultima copia: Hace 2 horas");
            lblEstadoCopia.setText("Estado: Guardado correctamente");
            lblUbicacionCopia.setText("Ubicacion: Servidor Local");
            btnRealizarCopia.setVisible(true);
            btnRealizarCopia.setManaged(true);
        } else {
            lblBackupTitle.setText("Informacion de Sesion");
            lblUltimaCopia.setText("Usuario: " + SessionManager.getInstance().getUsername());

            String roleDisplayName = role;
            if ("STOREKEEPER".equals(role)) {
                roleDisplayName = "Encargado de Almacen";
            } else if ("SELLER".equals(role)) {
                roleDisplayName = "Vendedor";
            }
            lblEstadoCopia.setText("Rol: " + roleDisplayName);
            lblUbicacionCopia.setText("Servidor: Conectado (Online)");

            btnRealizarCopia.setVisible(false);
            btnRealizarCopia.setManaged(false);
        }

        if ("STOREKEEPER".equals(role)) {
            ocultarComponente(btnQuickVenta);
            ocultarComponente(btnQuickCliente);
        } else if ("SELLER".equals(role)) {
            ocultarComponente(btnQuickInventario);
        }
    }

    private void ocultarComponente(Button button) {
        if (button != null) {
            button.setVisible(false);
            button.setManaged(false);
        }
    }

    @FXML
    private void handleRealizarCopia(ActionEvent event) {
    }

    @FXML
    private void handleVenta(ActionEvent event) {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/ventas/ventas.fxml");
    }

    @FXML
    private void handleInventario(ActionEvent event) {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/productos/productos.fxml");
    }

    @FXML
    private void handleCliente(ActionEvent event) {
        NavigationManager.getInstance().navegar("/com/store/inventario/views/clientes/clientes.fxml");
    }

    @FXML
    private void handleGuia(ActionEvent event) {
        Stage stage = (Stage) lblBienvenida.getScene().getWindow();
        DialogUtils.mostrarMensaje(
                stage,
                "Guia Rapida",
                "Bienvenido al sistema de inventario.\n\nAccesos basicos:\n- Ventas: Registra salidas de productos.\n- Inventario: Administra el catalogo de productos y stock.\n- Clientes: Registra y gestiona datos de clientes.\n\nSi necesitas asistencia adicional, usa el boton de Soporte.",
                "SISTEMA",
                "INFO",
                null,
                false,
                true
        );
    }

    @FXML
    private void handleSoporte(ActionEvent event) {
        Stage stage = (Stage) lblBienvenida.getScene().getWindow();
        DialogUtils.mostrarMensaje(
                stage,
                "Soporte Técnico",
                "Para asistencia o reportar problemas, comunícate con nosotros:\n\nCorreo: soporte@electrostore.com\nTeléfono: +51 987 654 321\nHorario: Lun-Vie 8:00 AM - 6:00 PM",
                "SOPORTE",
                "INFO",
                null,
                false,
                true
        );
    }

    public static class Actividad {
        private final String hora;
        private final String usuario;
        private final String accion;

        public Actividad(String hora, String usuario, String accion) {
            this.hora = hora;
            this.usuario = usuario;
            this.accion = accion;
        }

        public String getHora() { return hora; }
        public String getUsuario() { return usuario; }
        public String getAccion() { return accion; }
    }
}
