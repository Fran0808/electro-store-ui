package com.store.inventario.module.person.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.store.inventario.shared.config.ApiConfig;
import com.store.inventario.shared.model.PageResponse;
import com.store.inventario.module.person.request.CreateEmployeeRequest;
import com.store.inventario.module.person.model.entity.Employee;
import com.store.inventario.module.person.request.UpdateEmployeeRequest;
import com.store.inventario.security.SessionManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EmployeeService {

    private static final String URL = ApiConfig.BASE_URL + "/employees";
    private final HttpClient client;
    private final Gson gson;

    public EmployeeService() {
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public PageResponse<Employee> obtenerEmpleados() {
        return obtenerEmpleados(0, 1000);
    }

    public PageResponse<Employee> obtenerEmpleados(int page, int size) {
        try {
            String url = URL + "?page=" + page + "&size=" + size;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener empleados: HTTP " + response.statusCode());
            }
            Type type = new TypeToken<PageResponse<Employee>>() {}.getType();
            return gson.fromJson(response.body(), type);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Employee crearEmpleado(CreateEmployeeRequest createRequest) {
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
                throw new RuntimeException("Error al crear empleado: HTTP " + response.statusCode());
            }
            return gson.fromJson(response.body(), Employee.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Employee actualizarEmpleado(String code, UpdateEmployeeRequest updateRequest) {
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
                throw new RuntimeException("Error al actualizar empleado: HTTP " + response.statusCode());
            }
            return gson.fromJson(response.body(), Employee.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void eliminarEmpleado(String code) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/" + code))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al eliminar empleado: HTTP " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
