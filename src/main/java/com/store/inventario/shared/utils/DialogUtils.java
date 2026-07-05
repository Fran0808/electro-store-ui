package com.store.inventario.shared.utils;

import com.store.inventario.component.ModalController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class DialogUtils {

    public static void mostrarMensaje(Stage ownerStage, String titulo, String mensaje, String categoria, String icono, Runnable accionConfirmar) {
        mostrarMensaje(ownerStage, titulo, mensaje, categoria, icono, accionConfirmar, true, true);
    }

    public static void mostrarMensaje(Stage ownerStage, String titulo, String mensaje, String categoria, String icono, Runnable accionConfirmar, boolean mostrarCancelar, boolean mostrarConfirmar) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogUtils.class.getResource("/views/component/custom-dialog.fxml"));
            VBox modalRoot = loader.load();

            ModalController controller = loader.getController();
            
            controller.setTitulo(titulo);
            controller.setMensaje(mensaje);
            controller.setCategoria(categoria);
            controller.setIcono(icono);
            controller.mostrarBotonCancelar(mostrarCancelar);
            controller.mostrarBotonConfirmar(mostrarConfirmar);

            Stage dialogStage = new Stage();
            WindowUtils.applyIcon(dialogStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(ownerStage);
            dialogStage.setTitle(titulo);
            dialogStage.setResizable(false);

            controller.getBtnCancelar().setOnAction(event -> {
                dialogStage.close();
            });

            controller.getBtnConfirmar().setOnAction(event -> {
                if (accionConfirmar != null) {
                    accionConfirmar.run();
                }
                dialogStage.close();
            });

            Scene scene = new Scene(modalRoot);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
