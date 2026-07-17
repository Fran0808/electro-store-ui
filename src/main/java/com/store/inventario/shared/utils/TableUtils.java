package com.store.inventario.shared.utils;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;

public class TableUtils {

    public static <T> void habilitarDobleClicParaCopiar(TableView<T> tabla) {
        tabla.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                @SuppressWarnings("rawtypes")
                TablePosition position = tabla.getFocusModel().getFocusedCell();
                if (position != null && position.getRow() >= 0) {
                    TableColumn<T, ?> columna = position.getTableColumn();
                    int fila = position.getRow();
                    if (columna != null && fila < tabla.getItems().size()) {
                        Object valorCelda = columna.getCellData(fila);
                        if (valorCelda != null) {
                            String textoACopiar = valorCelda.toString();
                            Clipboard clipboard = Clipboard.getSystemClipboard();
                            ClipboardContent content = new ClipboardContent();
                            content.putString(textoACopiar);
                            clipboard.setContent(content);

                            NotificationUtils.showToast(tabla.getScene().getWindow(), "¡Copiado al portapapeles!");
                        }
                    }
                }
            }
        });
    }
}
