package com.store.inventario.service.producto;

import com.store.inventario.model.PageResponse;
import com.store.inventario.model.producto.CreateProductRequest;
import com.store.inventario.model.producto.UpdateProductRequest;
import com.store.inventario.model.producto.Producto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.store.inventario.security.SessionManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ProductoService {
    private static final String URL = "http://localhost:8080/api/products";
    private final HttpClient client;
    private final Gson gson;

    public ProductoService() {
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public PageResponse<Producto> obtenerProductos() {
        return obtenerProductos("", 0, 1000);
    }

    public PageResponse<Producto> obtenerProductos(int page, int size) {
        return obtenerProductos("", page, size);
    }

    public PageResponse<Producto> obtenerProductos(String search, int page, int size) {
        try {
            String paginatedUrl = URL + "?page=" + page + "&size=" + size;
            if (search != null && !search.trim().isEmpty()) {
                paginatedUrl += "&search=" + java.net.URLEncoder.encode(search.trim(), java.nio.charset.StandardCharsets.UTF_8);
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

            Type type = new TypeToken<PageResponse<Producto>>() {}.getType();
            return gson.fromJson(response.body(), type);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Producto crearProducto(CreateProductRequest createRequest) {
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

            return gson.fromJson(response.body(), Producto.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Producto actualizarProducto(String code, UpdateProductRequest updateRequest) {
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

            return gson.fromJson(response.body(), Producto.class);
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
}
