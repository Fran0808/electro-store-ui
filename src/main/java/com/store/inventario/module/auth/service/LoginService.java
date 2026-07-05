package com.store.inventario.module.auth.service;

import com.google.gson.Gson;
import com.store.inventario.shared.config.ApiConfig;
import com.store.inventario.module.auth.request.LoginRequest;
import com.store.inventario.module.auth.response.LoginResponse;
import com.store.inventario.security.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginService {
    private static final String URL_LOGIN = ApiConfig.BASE_URL + "/auth/login";
    private final HttpClient client;
    private final Gson gson;

    public LoginService() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public boolean autenticar(String username, String password) {
        try {
            LoginRequest loginRequest = new LoginRequest(username, password);
            String jsonRequestBody = gson.toJson(loginRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_LOGIN))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                LoginResponse loginResponse = gson.fromJson(response.body(), LoginResponse.class);

                SessionManager.getInstance().guardarSesion(loginResponse.getToken(), loginResponse.getAuth().getUsername(), loginResponse.getAuth().getRole());
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
