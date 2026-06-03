package com.labquest.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.labquest.backend.config.LabQuestProperties;
import com.labquest.backend.dtos.upload.UploadResponse;
import com.labquest.backend.exception.ApiException;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "webp");

    private final LabQuestProperties properties;

    public FileStorageService(LabQuestProperties properties) {
        this.properties = properties;
    }

    public UploadResponse storeImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Arquivo vazio.");
        }

        String originalName = file.getOriginalFilename() == null ? "imagem" : file.getOriginalFilename();
        String extension = extractExtension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Formato de imagem nao suportado.");
        }

        String fileName = UUID.randomUUID() + "." + extension.toLowerCase();
        Path imagesDir = Path.of(properties.uploadDir(), "imagens");
        Path target = imagesDir.resolve(fileName);

        try {
            Files.createDirectories(imagesDir);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Nao foi possivel salvar a imagem.");
        }

        String relativePath = "imagens/" + fileName;
        String publicUrl = properties.publicBaseUrl() + "/uploads/" + relativePath;
        return new UploadResponse(fileName, relativePath, publicUrl);
    }

    private String extractExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index + 1);
    }
}
