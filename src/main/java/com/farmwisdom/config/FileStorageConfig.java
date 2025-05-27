package com.farmwisdom.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.file")
public class FileStorageConfig {
    private String uploadDir;
    private String allowedFileTypes;
    private long maxFileSize;
    private String baseUrl;
}