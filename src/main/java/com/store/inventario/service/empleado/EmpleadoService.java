package com.store.inventario.service.empleado;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.store.inventario.model.PageResponse;
import com.store.inventario.model.empleado.Empleado;
import com.store.inventario.model.producto.Producto;
import com.store.inventario.model.producto.UpdateProductRequest;
import com.store.inventario.security.SessionManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EmpleadoService {

    private static final String URL = "http://localhost:8000/api/employees";
    private final HttpClient client;
    private final Gson gson;

    public EmpleadoService(){
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public PageResponse<Empleado> obtenerEmpleados() {

        try {
            HttpRequest requst = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Authorization", "Bearer" + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener empleados: HTTP" + response.statusCode());
            }

            Type type = new TypeToken<PageResponse<Producto>>() {}.getType();
            return gson.fromJson(response.body(), type);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        public Empleado crearEmpleado(CreateEmployeeRequest createRequest) {
            try {
                String json = gson.toJson(createRequest);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(URL))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                        .POST(HttpRequest.BodyPublisher.ofString(json))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() >= 400) {
                    throw new RuntimeException("Error al crear producto: HTTP " + response.statusCode());
                }
                return gson.fromJson(response.body(), Empleado.class);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Empleado actualizarEmpleado(String code, UpdateProductRequest updateRequest){
        try {
            String json = gson.toJson(updateRequest);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/" + code))
                    .header("Content-Type", "aplication/json")
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .PUT(HttpRequest.BodyPublisher.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al actualizar empleado: HTTP " + response.statusCode());
            }

            return gson.fromJson(response.body(), Empleado.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void eliminarEmpleado(String code) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/" + code))
                    .header("Authorization", "Bearer" + SessionManager.getInstance().getToken())
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400){
                throw new RuntimeException("Error al eliminar empleado: HTTP" + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
