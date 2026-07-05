package com.store.inventario.module.person.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.store.inventario.shared.config.ApiConfig;
import com.store.inventario.shared.model.PageResponse;
import com.store.inventario.module.person.model.entity.Customer;
import com.store.inventario.module.person.request.CreateCustomerRequest;
import com.store.inventario.module.person.request.UpdateCustomerRequest;
import com.store.inventario.security.SessionManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.store.inventario.module.person.model.entity.CustomerMetrics;

public class CustomerService {
    private static final String URL = ApiConfig.BASE_URL + "/customers";
    private final HttpClient client;
    private final Gson gson;

    public CustomerMetrics obtenerMetricas() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/metrics"))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener métricas de clientes: HTTP " + response.statusCode());
            }
            return gson.fromJson(response.body(), CustomerMetrics.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public CustomerService() {
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public PageResponse<Customer> listar(int page, int size) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "?page=" + page + "&size=" + size))
                    .header("Authorization",
                            "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener clientes: HTTP " + response.statusCode());
            }

            Type type = new TypeToken<PageResponse<Customer>>() {}.getType();
            return gson.fromJson(response.body(), type);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public PageResponse<Customer> obtenerClientes(String search, int page, int size) {
        try{
            String urlStr = URL + "?page=" + page + "&size=" + size;
            if (search != null && !search.trim().isEmpty()) {
                urlStr += "&search=" + java.net.URLEncoder.encode(search.trim(), java.nio.charset.StandardCharsets.UTF_8);
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener clientes: HTTP " + response.statusCode());
            }
            Type type = new TypeToken<PageResponse<Customer>>() {}.getType();
            return gson.fromJson(response.body(), type);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Customer crearCliente(CreateCustomerRequest createRequest) {
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
                throw new RuntimeException("Error al crear cliente: HTTP " + response.statusCode());
            }

            return gson.fromJson(response.body(), Customer.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Customer actualizarCliente(String code, UpdateCustomerRequest updateRequest) {
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
                throw new RuntimeException("Error al actualizar cliente: HTTP " + response.statusCode());
            }

            return gson.fromJson(response.body(), Customer.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void eliminarCliente(String code) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/" + code))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al eliminar cliente: HTTP " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
