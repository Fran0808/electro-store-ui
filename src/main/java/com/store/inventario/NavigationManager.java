package com.store.inventario;

import java.util.function.Consumer;

public class NavigationManager {
    private static NavigationManager instance;
    private Consumer<String> onNavegar;

    private NavigationManager() {}

    public static NavigationManager getInstance() {
        if (instance == null) instance = new NavigationManager();
        return instance;
    }

    public void setOnNavegar(Consumer<String> handler) {
        this.onNavegar = handler;
    }

    public void navegar(String fxmlPath) {
        if (onNavegar != null) onNavegar.accept(fxmlPath);
    }

}
