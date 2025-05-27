package com.farmwisdom.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String subDir);
    void deleteFile(String filePath);
    String getFileUrl(String fileName);
    boolean isValidFileType(String fileName);
}