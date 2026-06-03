package com.labquest.backend.controller;

import com.labquest.backend.dtos.upload.UploadResponse;
import com.labquest.backend.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private final FileStorageService fileStorageService;

    public UploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/images")
    @ResponseStatus(HttpStatus.CREATED)
    public UploadResponse uploadImage(@RequestParam("file") MultipartFile file) {
        return fileStorageService.storeImage(file);
    }
}
