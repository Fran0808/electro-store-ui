package com.store.inventario.module.product.service;

import com.store.inventario.shared.config.ApiConfig;
import com.store.inventario.shared.model.PageResponse;
import com.store.inventario.module.product.request.CreateProductRequest;
import com.store.inventario.module.product.request.UpdateProductRequest;
import com.store.inventario.module.product.model.entity.Product;
import com.store.inventario.module.product.model.entity.ProductMetrics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.store.inventario.security.SessionManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ProductService {
    private static final String URL = ApiConfig.BASE_URL + "/products";
    private final HttpClient client;
    private final Gson gson;

    public ProductService() {
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public PageResponse<Product> obtenerProductos() {
        return obtenerProductos("", 0, 1000);
    }

    public PageResponse<Product> obtenerProductos(int page, int size) {
        return obtenerProductos("", page, size);
    }

    public PageResponse<Product> obtenerProductos(String search, int page, int size) {
        return obtenerProductos(search, null, null, null, page, size);
    }

    public PageResponse<Product> obtenerProductos(String search, String categoryName, String brand, String stockStatus, int page, int size) {
        try {
            String paginatedUrl = URL + "?page=" + page + "&size=" + size;
            if (search != null && !search.trim().isEmpty()) {
                paginatedUrl += "&search=" + java.net.URLEncoder.encode(search.trim(), java.nio.charset.StandardCharsets.UTF_8);
            }
            if (categoryName != null && !categoryName.trim().isEmpty() && !"Todas".equalsIgnoreCase(categoryName.trim()) && !"Categoría".equalsIgnoreCase(categoryName.trim())) {
                paginatedUrl += "&categoryName=" + java.net.URLEncoder.encode(categoryName.trim(), java.nio.charset.StandardCharsets.UTF_8);
            }
            if (brand != null && !brand.trim().isEmpty() && !"Todas".equalsIgnoreCase(brand.trim()) && !"Marca".equalsIgnoreCase(brand.trim())) {
                paginatedUrl += "&brand=" + java.net.URLEncoder.encode(brand.trim(), java.nio.charset.StandardCharsets.UTF_8);
            }
            if (stockStatus != null && !stockStatus.trim().isEmpty() && !"Todos".equalsIgnoreCase(stockStatus.trim())) {
                paginatedUrl += "&stockStatus=" + java.net.URLEncoder.encode(stockStatus.trim(), java.nio.charset.StandardCharsets.UTF_8);
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(paginatedUrl))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener productos: HTTP " + response.statusCode());
            }

            Type type = new TypeToken<PageResponse<Product>>() {}.getType();
            return gson.fromJson(response.body(), type);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Product crearProducto(CreateProductRequest createRequest) {
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
                throw new RuntimeException("Error al crear producto: HTTP " + response.statusCode());
            }

            return gson.fromJson(response.body(), Product.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Product actualizarProducto(String code, UpdateProductRequest updateRequest) {
        try {
            String json = gson.toJson(updateRequest);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/" + code))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al actualizar producto: HTTP " + response.statusCode());
            }

            return gson.fromJson(response.body(), Product.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void eliminarProducto(String code) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/" + code))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al eliminar producto: HTTP " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public ProductMetrics obtenerMetricas() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/metrics"))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener métricas de productos: HTTP " + response.statusCode());
            }

            return gson.fromJson(response.body(), ProductMetrics.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public java.util.List<String> obtenerMarcas() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/brands"))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener marcas: HTTP " + response.statusCode());
            }

            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<java.util.List<String>>() {}.getType();
            return gson.fromJson(response.body(), type);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
