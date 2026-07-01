package com.store.inventario.service.guia;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.store.inventario.config.ApiConfig;
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
    private static final String URL = ApiConfig.BASE_URL + "/inventory-guides";
    private final HttpClient client;
    private final Gson gson;

    public InventoryGuideService() {
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public PageResponse<InventoryGuide> obtenerGuias() {
        return obtenerGuias(null, null, null, null, 0, 10);
    }

    public PageResponse<InventoryGuide> obtenerGuias(int page, int size) {
        return obtenerGuias(null, null, null, null, page, size);
    }

    public PageResponse<InventoryGuide> obtenerGuias(String search, String type, String startDate, String endDate, int page, int size) {
        try {
            StringBuilder urlBuilder = new StringBuilder(URL);
            urlBuilder.append("?page=").append(page).append("&size=").append(size).append("&sort=guideDate,desc");
            if (search != null && !search.trim().isEmpty()) {
                urlBuilder.append("&search=").append(java.net.URLEncoder.encode(search.trim(), java.nio.charset.StandardCharsets.UTF_8));
            }
            if (type != null && !type.trim().isEmpty() && !type.equalsIgnoreCase("Todos")) {
                urlBuilder.append("&type=").append(java.net.URLEncoder.encode(type.trim(), java.nio.charset.StandardCharsets.UTF_8));
            }
            if (startDate != null && !startDate.trim().isEmpty()) {
                urlBuilder.append("&startDate=").append(java.net.URLEncoder.encode(startDate.trim(), java.nio.charset.StandardCharsets.UTF_8));
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                urlBuilder.append("&endDate=").append(java.net.URLEncoder.encode(endDate.trim(), java.nio.charset.StandardCharsets.UTF_8));
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBuilder.toString()))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener guías de inventario: HTTP " + response.statusCode());
            }

            Type typeToken = new TypeToken<PageResponse<InventoryGuide>>() {}.getType();
            return gson.fromJson(response.body(), typeToken);

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

    public InventoryGuide obtenerGuiaPorCodigo(String code) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/" + code))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener detalle de guía: HTTP " + response.statusCode());
            }

            return gson.fromJson(response.body(), InventoryGuide.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
