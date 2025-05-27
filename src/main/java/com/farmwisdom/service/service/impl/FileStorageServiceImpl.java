package com.farmwisdom.service.impl;

import com.farmwisdom.config.FileStorageConfig;
import com.farmwisdom.exception.FileStorageException;
import com.farmwisdom.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageConfig fileStorageConfig;

    @Override
    public String storeFile(MultipartFile file, String subDir) {
        if (file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (!isValidFileType(originalFileName)) {
            throw new FileStorageException("Invalid file type. Allowed types: " + fileStorageConfig.getAllowedFileTypes());
        }

        try {
            // 生成唯一文件名
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // 创建目标目录
            Path targetLocation = Paths.get(fileStorageConfig.getUploadDir(), subDir).toAbsolutePath().normalize();
            Files.createDirectories(targetLocation);

            // 保存文件
            Path filePath = targetLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Stored file {} in {}", newFileName, targetLocation);
            return subDir + "/" + newFileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFileName, ex);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path targetLocation = Paths.get(fileStorageConfig.getUploadDir(), filePath).toAbsolutePath().normalize();
            Files.deleteIfExists(targetLocation);
            log.info("Deleted file {}", targetLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + filePath, ex);
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        return fileStorageConfig.getBaseUrl() + "/" + fileName;
    }

    @Override
    public boolean isValidFileType(String fileName) {
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        String[] allowedTypes = fileStorageConfig.getAllowedFileTypes().split(",");
        return Arrays.asList(allowedTypes).contains(fileExtension);
    }
}