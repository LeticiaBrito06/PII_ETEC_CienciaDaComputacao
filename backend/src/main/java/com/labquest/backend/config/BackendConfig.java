package com.labquest.backend.config;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(LabQuestProperties.class)
public class BackendConfig implements WebMvcConfigurer {

    private final LabQuestProperties properties;

    public BackendConfig(LabQuestProperties properties) throws Exception {
        this.properties = properties;
        Files.createDirectories(resolveUploadDir());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDirPath = resolveUploadDir();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDirPath + "/")
                .setCachePeriod(3600);
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
