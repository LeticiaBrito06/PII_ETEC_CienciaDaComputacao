package com.labquest.backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.labquest.backend.config.LabQuestProperties;
import com.labquest.backend.exception.ApiException;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final LabQuestProperties properties;

    public PublicController(LabQuestProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "ok",
                "service", "labquest-backend",
                "timestamp", LocalDateTime.now());
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path uploadDir = resolveUploadDir();
            Path imageFile = uploadDir.resolve("imagens").resolve(filename).normalize();

            if (!imageFile.startsWith(uploadDir)) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Acesso negado.");
            }

            if (!Files.exists(imageFile)) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Imagem nao encontrada.");
            }

            Resource resource = new FileSystemResource(imageFile);
            String contentType = Files.probeContentType(imageFile);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE,
                            contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                    .body(resource);
        } catch (IOException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao ler a imagem.");
        }
    }

    private Path resolveUploadDir() {
        Path configuredPath = Path.of(properties.uploadDir());

        if (configuredPath.isAbsolute()) {
            return configuredPath.normalize();
        }

        Path currentDirectoryPath = configuredPath.toAbsolutePath().normalize();
        Path backendDirectoryPath = Path.of("backend").resolve(configuredPath).toAbsolutePath().normalize();

        if (!Files.exists(currentDirectoryPath) && Files.exists(backendDirectoryPath)) {
            return backendDirectoryPath;
        }

        return currentDirectoryPath;
    }
}
