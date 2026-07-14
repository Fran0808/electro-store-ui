package com.store.inventario.module.report.service;

import com.store.inventario.shared.config.ApiConfig;
import com.store.inventario.security.SessionManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ReportService {

    private final HttpClient client;
    private static final String BASE_URL = ApiConfig.BASE_URL + "/reports";

    public ReportService() {
        this.client = HttpClient.newHttpClient();
    }

    public byte[] descargarReporteVentas(int anio) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/sales-report/" + anio))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("Error en el servidor: HTTP " + response.statusCode());
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error de conexión al descargar el reporte", e);
        }
    }
}