package com.store.inventario.service.venta;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.store.inventario.config.ApiConfig;
import com.store.inventario.model.PageResponse;
import com.store.inventario.model.ventas.CreateSaleRequest;
import com.store.inventario.model.ventas.Venta;
import com.store.inventario.model.ventas.VentaMetrics;
import com.store.inventario.security.SessionManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class VentaService {

    private static final String URL = ApiConfig.BASE_URL + "/sales";
    private final HttpClient client;
    private final Gson gson;

    public VentaService(){
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public PageResponse<Venta> obtenerVenta() {
        return obtenerVenta("", 0, 20);
    }

    public PageResponse<Venta> obtenerVenta(String search) {
        return obtenerVenta(search, 0, 20);
    }

    public PageResponse<Venta> obtenerVenta(String search, int page, int size) {
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
            Type type = new TypeToken<PageResponse<Venta>>() {}.getType();
            return gson.fromJson(response.body(), type);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Venta crearVenta(CreateSaleRequest createRequest){
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
            return gson.fromJson(response.body(), Venta.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public VentaMetrics obtenerMetricas() {
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

            return gson.fromJson(response.body(), VentaMetrics.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
