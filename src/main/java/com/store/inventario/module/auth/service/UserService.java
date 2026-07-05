package com.store.inventario.module.auth.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.store.inventario.shared.config.ApiConfig;
import com.store.inventario.shared.model.PageResponse;
import com.store.inventario.module.auth.model.entity.User;
import com.store.inventario.security.SessionManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UserService {
    private static final String URL = ApiConfig.BASE_URL + "/users";
    private final HttpClient client;
    private final Gson gson;

    public UserService() {
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public PageResponse<User> obtenerUsuarios() {
        return obtenerUsuarios(0, 1000);
    }

    public PageResponse<User> obtenerUsuarios(int page, int size) {
        try {
            String url = URL + "?page=" + page + "&size=" + size;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al obtener usuarios: HTTP " + response.statusCode());
            }

            Type type = new TypeToken<PageResponse<User>>() {}.getType();
            return gson.fromJson(response.body(), type);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public User crearUsuario(User user) {
        try {
            String json = gson.toJson(user);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al crear usuario: HTTP " + response.statusCode());
            }

            return gson.fromJson(response.body(), User.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public User actualizarUsuario(String code, User user) {
        try {
            String json = gson.toJson(user);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/" + code))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al actualizar usuario: HTTP " + response.statusCode());
            }

            return gson.fromJson(response.body(), User.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void eliminarUsuario(String code) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/" + code))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al eliminar usuario: HTTP " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public User activarUsuario(String code) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/activate/" + code))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .method("PATCH", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al activar usuario: HTTP " + response.statusCode() + " - " + response.body());
            }

            return gson.fromJson(response.body(), User.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public User desactivarUsuario(String code) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/deactivate/" + code))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .method("PATCH", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al desactivar usuario: HTTP " + response.statusCode() + " - " + response.body());
            }

            return gson.fromJson(response.body(), User.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
