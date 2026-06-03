package com.store.inventario.service.guia;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.store.inventario.model.PageResponse;
import com.store.inventario.model.guia.CreateInventoryGuideRequest;
import com.store.inventario.model.guia.InventoryGuide;
import com.store.inventario.security.SessionManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class InventoryGuideService {
    private static final String URL = "http://localhost:8080/api/inventory-guides";
    private final HttpClient client;
    private final Gson gson;

    public InventoryGuideService() {
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public PageResponse<InventoryGuide> obtenerGuias() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener guías de inventario: HTTP " + response.statusCode());
            }

            Type type = new TypeToken<PageResponse<InventoryGuide>>() {}.getType();
            return gson.fromJson(response.body(), type);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public InventoryGuide crearGuia(CreateInventoryGuideRequest createRequest) {
        try {
            String json = gson.toJson(createRequest);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al crear guía de inventario: HTTP " + response.statusCode() + " - " + response.body());
            }

            return gson.fromJson(response.body(), InventoryGuide.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
