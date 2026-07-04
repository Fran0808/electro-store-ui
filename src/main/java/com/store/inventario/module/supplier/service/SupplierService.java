package com.store.inventario.module.supplier.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.store.inventario.config.ApiConfig;
import com.store.inventario.model.PageResponse;
import com.store.inventario.module.supplier.request.CreateSupplierRequest;
import com.store.inventario.module.supplier.model.entity.Supplier;
import com.store.inventario.module.supplier.request.UpdateSupplierRequest;
import com.store.inventario.security.SessionManager;

import java.io.IOException;
import java.net.URI;
import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.store.inventario.module.supplier.model.entity.SupplierMetrics;

public class SupplierService {

    private static final String URL = ApiConfig.BASE_URL + "/suppliers";

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public SupplierMetrics obtenerMetricas() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/metrics"))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener métricas de proveedores: HTTP " + response.statusCode());
            }
            return gson.fromJson(response.body(), SupplierMetrics.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public PageResponse<Supplier> listar(int page, int size) {
        return listar(null, page, size);
    }

    public PageResponse<Supplier> listar(String search, int page, int size) {
        try {
            String urlStr = URL + "?page=" + page + "&size=" + size;
            if (search != null && !search.trim().isEmpty()) {
                urlStr += "&search=" + java.net.URLEncoder.encode(search.trim(), java.nio.charset.StandardCharsets.UTF_8);
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .header("Authorization",
                            "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener proveedores: HTTP " + response.statusCode());
            }

            Type type = new TypeToken<PageResponse<Supplier>>() {}.getType();
            return gson.fromJson(response.body(), type);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Supplier crear(CreateSupplierRequest data) {
        try {
            String json = gson.toJson(data);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al crear proveedor: HTTP " + response.statusCode());
            }
            return gson.fromJson(response.body(), Supplier.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Supplier actualizar(String code, UpdateSupplierRequest data) {
        try {
            String json = gson.toJson(data);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/" + code))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al actualizar proveedor: HTTP " + response.statusCode());
            }
            return gson.fromJson(response.body(), Supplier.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void eliminar(String code) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/" + code))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al eliminar proveedor: HTTP " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
