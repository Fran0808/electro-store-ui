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
        return obtenerVenta("", 0, 20);
    }

    public PageResponse<Sale> obtenerVenta(String search) {
        return obtenerVenta(search, 0, 20);
    }

    public PageResponse<Sale> obtenerVenta(String search, int page, int size) {
        try {
            String urlString = URL + "?page=" + page + "&size=" + size;
            if (search != null && !search.trim().isEmpty()) {
                urlString += "&search=" + java.net.URLEncoder.encode(search.trim(), StandardCharsets.UTF_8);
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
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
}
