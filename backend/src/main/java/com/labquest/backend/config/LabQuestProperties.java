package com.labquest.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "labquest")
public record LabQuestProperties(Jwt jwt, String uploadDir, String publicBaseUrl) {

    public record Jwt(String secret, long expirationMs) {
    }
}
