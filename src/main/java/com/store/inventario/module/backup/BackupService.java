package com.store.inventario.module.backup;

import com.store.inventario.shared.config.ApiConfig;
import com.store.inventario.security.SessionManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BackupService {
    private static final String URL_BACKUP = ApiConfig.BASE_URL + "/backup";
    private final HttpClient client;

    public BackupService() {
        this.client = HttpClient.newHttpClient();
    }

    public File descargarBackup(File directorioDestino) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BACKUP))
                .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException("Error al generar el backup. Código de respuesta: " + response.statusCode());
        }

        String fileName = "backup.bak";
        String contentDisposition = response.headers().firstValue("content-disposition").orElse("");
        if (contentDisposition.contains("filename=")) {
            String extracted = contentDisposition.split("filename=")[1].replace("\"", "").trim();
            if (!extracted.isEmpty()) {
                fileName = extracted;
            }
        }

        File archivoDestino = new File(directorioDestino, fileName);
        java.nio.file.Files.write(archivoDestino.toPath(), response.body());

        return archivoDestino;
    }
}
