package com.store.inventario.module.sale.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.store.inventario.shared.config.ApiConfig;
import com.store.inventario.shared.model.PageResponse;
import com.store.inventario.module.sale.request.CreateSaleRequest;
import com.store.inventario.module.sale.model.entity.Sale;
import com.store.inventario.module.sale.model.entity.SaleMetrics;
import com.store.inventario.security.SessionManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class SaleService {

    private static final String URL = ApiConfig.BASE_URL + "/sales";
    private final HttpClient client;
    private final Gson gson;

    public SaleService(){
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public PageResponse<Sale> obtenerVenta() {
        return obtenerVenta("", null, null, null, 0, 20);
    }

    public PageResponse<Sale> obtenerVenta(String search) {
        return obtenerVenta(search, null, null, null, 0, 20);
    }

    public PageResponse<Sale> obtenerVenta(String search, int page, int size) {
        return obtenerVenta(search, null, null, null, page, size);
    }

    public PageResponse<Sale> obtenerVenta(String search, String user, String startDate, String endDate, int page, int size) {
        try {
            StringBuilder urlBuilder = new StringBuilder(URL);
            urlBuilder.append("?page=").append(page).append("&size=").append(size);
            if (search != null && !search.trim().isEmpty()) {
                urlBuilder.append("&search=").append(java.net.URLEncoder.encode(search.trim(), StandardCharsets.UTF_8));
            }
            if (user != null && !user.trim().isEmpty() && !"Todos".equalsIgnoreCase(user.trim())) {
                urlBuilder.append("&user=").append(java.net.URLEncoder.encode(user.trim(), StandardCharsets.UTF_8));
            }
            if (startDate != null && !startDate.trim().isEmpty()) {
                urlBuilder.append("&startDate=").append(java.net.URLEncoder.encode(startDate.trim(), StandardCharsets.UTF_8));
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                urlBuilder.append("&endDate=").append(java.net.URLEncoder.encode(endDate.trim(), StandardCharsets.UTF_8));
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBuilder.toString()))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener ventas: HTTP " + response.statusCode());
            }
            Type type = new TypeToken<PageResponse<Sale>>() {}.getType();
            return gson.fromJson(response.body(), type);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public java.util.List<String> obtenerVendedoresFiltro() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/sellers"))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                return java.util.Collections.emptyList();
            }
            Type type = new TypeToken<java.util.List<String>>() {}.getType();
            return gson.fromJson(response.body(), type);
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    public Sale crearVenta(CreateSaleRequest createRequest){
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
                throw new RuntimeException("Error al crear una venta: HTTP " + response.statusCode() + " - " + response.body());
            }
            return gson.fromJson(response.body(), Sale.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public SaleMetrics obtenerMetricas() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/dashboard"))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener métricas de ventas: HTTP " + response.statusCode());
            }

            return gson.fromJson(response.body(), SaleMetrics.class);
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
                throw new RuntimeException("Error al obtener resumen diario de ventas: HTTP " + response.statusCode());
            }

            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.List<com.store.inventario.shared.model.DailySummary>>() {}.getType();
            return gson.fromJson(response.body(), type);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public java.util.List<com.store.inventario.shared.model.TopProduct> obtenerTopProductos(int limit) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/top-products?limit=" + limit))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener productos más vendidos: HTTP " + response.statusCode());
            }

            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.List<com.store.inventario.shared.model.TopProduct>>() {}.getType();
            return gson.fromJson(response.body(), type);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
