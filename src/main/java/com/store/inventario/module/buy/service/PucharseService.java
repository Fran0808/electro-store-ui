package com.store.inventario.module.buy.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.store.inventario.shared.config.ApiConfig;
import com.store.inventario.shared.model.PageResponse;
import com.store.inventario.module.buy.model.entity.Purchase;
import com.store.inventario.module.buy.model.entity.PurchaseDashboard;
import com.store.inventario.module.buy.request.CreatePurchaseRequest;
import com.store.inventario.module.buy.model.entity.PurchaseMetrics;
import com.store.inventario.security.SessionManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PucharseService {
    private static final String URL = ApiConfig.BASE_URL + "/purchases";
    private final HttpClient client;
    private final Gson gson;

    public PucharseService() {
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public PageResponse<Purchase> obtenerCompra() {
        return obtenerCompra("", 0, 20);
    }

    public PageResponse<Purchase> obtenerCompra(String search) {
        return obtenerCompra(search, 0, 20);
    }

    public PageResponse<Purchase> obtenerCompra(String search, int page, int size) {
        try {
            String urlString = URL + "?page=" + page + "&size=" + size;
            if (search != null && !search.trim().isEmpty()) {
                urlString += "&search=" + java.net.URLEncoder.encode(search.trim(), java.nio.charset.StandardCharsets.UTF_8);
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener compras: HTTP " + response.statusCode());
            }
            Type type = new TypeToken<PageResponse<Purchase>>() {}.getType();
            return gson.fromJson(response.body(), type);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Purchase crearCompra(CreatePurchaseRequest createRequest) {
        try{
            String json = gson.toJson(createRequest);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al crear una compra: HTTP " + response.statusCode());
            }
            return gson.fromJson(response.body(), Purchase.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public PurchaseDashboard obtenerDashboard() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/dashboard"))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener dashboard de compras: HTTP " + response.statusCode());
            }

            return gson.fromJson(response.body(), PurchaseDashboard.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public PurchaseMetrics obtenerMetricas() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/metrics"))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener métricas de compras: HTTP " + response.statusCode());
            }

            return gson.fromJson(response.body(), PurchaseMetrics.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public java.util.List<com.store.inventario.shared.model.DailySummary> obtenerResumenDiario(int days) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/daily-summary?days=" + days))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener resumen diario de compras: HTTP " + response.statusCode());
            }

            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.List<com.store.inventario.shared.model.DailySummary>>() {}.getType();
            return gson.fromJson(response.body(), type);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
