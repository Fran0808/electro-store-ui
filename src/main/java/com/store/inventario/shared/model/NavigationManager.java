package com.store.inventario.shared.model;

import java.util.function.Consumer;

public class NavigationManager {
    private static NavigationManager instance;
    private Consumer<String> onNavegar;
    private Runnable onRefreshAlerts;

    private NavigationManager() {}

    public static NavigationManager getInstance() {
        if (instance == null) instance = new NavigationManager();
        return instance;
    }

    public void setOnNavegar(Consumer<String> handler) {
        this.onNavegar = handler;
    }

    public void setOnRefreshAlerts(Runnable handler) {
        this.onRefreshAlerts = handler;
    }

    public void refreshAlerts() {
        if (onRefreshAlerts != null) {
            onRefreshAlerts.run();
        }
    }

    public void navegar(String fxmlPath) {
        if (onNavegar != null) onNavegar.accept(fxmlPath);
    }

}
