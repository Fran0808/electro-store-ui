package com.store.inventario.service.usuario;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.store.inventario.model.PageResponse;
import com.store.inventario.model.usuario.Usuario;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UsuarioService {
    private static final String URL = "http://localhost:8080/api/users";
    private final HttpClient client;
    private final Gson gson;

    public UsuarioService() {
        client = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public PageResponse<Usuario> obtenerUsuarios() {

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type type = new TypeToken<PageResponse<Usuario>>() {}.getType();

            return gson.fromJson(response.body(), type);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Usuario crearUsuario(Usuario usuario) {
        try {
            String json = gson.toJson(usuario);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error al crear usuario: HTTP " + response.statusCode());
            }

            return gson.fromJson(response.body(), Usuario.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
