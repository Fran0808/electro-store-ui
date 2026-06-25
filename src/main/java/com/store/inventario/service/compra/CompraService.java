package com.store.inventario.service.compra;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.store.inventario.model.PageResponse;
import com.store.inventario.model.clientes.Cliente;
import com.store.inventario.model.compra.Compra;
import com.store.inventario.model.compra.CreatePurchaseRequest;
import com.store.inventario.model.compra.PurchaseMetrics;
import com.store.inventario.security.SessionManager;
import com.store.inventario.service.clientes.ClienteService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CompraService {
    private static final String URL = "http://localhost:8080/api/purchases";
    private final HttpClient client;
    private final Gson gson;

    public CompraService() {
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public PageResponse<Compra> obtenerCompra() {
        return obtenerCompra("", 0, 20);
    }

    public PageResponse<Compra> obtenerCompra(String search) {
        return obtenerCompra(search, 0, 20);
    }

    public PageResponse<Compra> obtenerCompra(String search, int page, int size) {
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
            Type type = new TypeToken<PageResponse<Compra>>() {}.getType();
            return gson.fromJson(response.body(), type);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Compra crearCompra(CreatePurchaseRequest createRequest) {
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
            return gson.fromJson(response.body(), Compra.class);
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
}
