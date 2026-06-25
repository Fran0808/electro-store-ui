package com.store.inventario.config;

import java.io.InputStream;
import java.util.Properties;

public class ApiConfig {
    public static final String BASE_URL;

    static {
        String url = "http://localhost:8080/api";
        try (InputStream input = ApiConfig.class.getResourceAsStream("/config.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                String configUrl = prop.getProperty("api.base.url");
                if (configUrl != null && !configUrl.trim().isEmpty()) {
                    url = configUrl.trim();
                }
            } else {
                System.err.println("No se encontró config.properties en el classpath. Usando URL por defecto: " + url);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar /config.properties. Usando URL por defecto: " + url);
            e.printStackTrace();
        }
        BASE_URL = url;
    }
}
