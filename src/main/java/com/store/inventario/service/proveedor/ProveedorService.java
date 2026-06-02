package com.store.inventario.service.proveedor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.store.inventario.model.PageResponse;
import com.store.inventario.model.proveedor.CreateProveedorRequest;
import com.store.inventario.model.proveedor.Proveedor;
import com.store.inventario.model.proveedor.UpdateProveedorRequest;
import com.store.inventario.security.SessionManager;

import java.io.IOException;
import java.net.URI;
import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ProveedorService {

    private static final String URL = "http://localhost:8080/api/suppliers";

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public PageResponse<Proveedor> listar(int page, int size) {

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "?page=" + page + "&size=" + size))
                    .header("Authorization",
                            "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            Type type = new TypeToken<PageResponse<Proveedor>>() {}.getType();

            return gson.fromJson(response.body(), type);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Proveedor crear(CreateProveedorRequest data) {
        try {
            String json = gson.toJson(data);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return gson.fromJson(response.body(), Proveedor.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Proveedor actualizar(String code, UpdateProveedorRequest data) {
        try {
            String json = gson.toJson(data);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/" + code))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return gson.fromJson(response.body(), Proveedor.class);
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
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
