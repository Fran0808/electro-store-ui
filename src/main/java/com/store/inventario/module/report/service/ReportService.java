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

    public byte[] descargarReporteVentas(String frequency, String date) {
        try {
            String url = BASE_URL + "/sales-report?frequency=" + frequency;
            if (date != null && !date.trim().isEmpty()) {
                url += "&date=" + date;
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() >= 400) {
                String errorBody = new String(response.body()).trim();
                String mensaje = errorBody.contains("message")
                        ? errorBody.replaceAll(".*\"message\":\"([^\"]+)\".*", "$1")
                        : "El servidor no pudo generar el reporte (HTTP " + response.statusCode() + ")";
                throw new RuntimeException(mensaje);
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error de conexión al descargar el reporte de ventas", e);
        }
    }

    public byte[] descargarReporteCompras(String frequency, String date) {
        try {
            String url = BASE_URL + "/purchases-report?frequency=" + frequency;
            if (date != null && !date.trim().isEmpty()) {
                url += "&date=" + date;
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() >= 400) {
                String errorBody = new String(response.body()).trim();
                String mensaje = errorBody.contains("message")
                        ? errorBody.replaceAll(".*\"message\":\"([^\"]+)\".*", "$1")
                        : "El servidor no pudo generar el reporte (HTTP " + response.statusCode() + ")";
                throw new RuntimeException(mensaje);
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error de conexión al descargar el reporte de compras", e);
        }
    }

    public byte[] descargarReporteKardex(String productCode, String startDate, String endDate, String frequency, String date) {
        try {
            StringBuilder urlBuilder = new StringBuilder(BASE_URL).append("/kardex-report?");
            boolean firstParam = true;
            if (productCode != null && !productCode.trim().isEmpty()) {
                urlBuilder.append("productCode=").append(productCode);
                firstParam = false;
            }
            if (startDate != null && !startDate.trim().isEmpty()) {
                if (!firstParam) urlBuilder.append("&");
                urlBuilder.append("startDate=").append(startDate);
                firstParam = false;
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                if (!firstParam) urlBuilder.append("&");
                urlBuilder.append("endDate=").append(endDate);
                firstParam = false;
            }
            if (frequency != null && !frequency.trim().isEmpty()) {
                if (!firstParam) urlBuilder.append("&");
                urlBuilder.append("frequency=").append(frequency);
                firstParam = false;
            }
            if (date != null && !date.trim().isEmpty()) {
                if (!firstParam) urlBuilder.append("&");
                urlBuilder.append("date=").append(date);
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBuilder.toString()))
                    .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                    .GET()
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() >= 400) {
                String errorBody = new String(response.body()).trim();
                String mensaje = errorBody.contains("message")
                        ? errorBody.replaceAll(".*\"message\":\"([^\"]+)\".*", "$1")
                        : "El servidor no pudo generar el reporte (HTTP " + response.statusCode() + ")";
                throw new RuntimeException(mensaje);
            }

            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error de conexión al descargar el reporte de kardex", e);
        }
    }
}