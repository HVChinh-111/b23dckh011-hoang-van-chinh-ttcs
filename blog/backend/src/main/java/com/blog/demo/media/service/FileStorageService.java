package com.blog.demo.media.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blog.demo.common.config.UploadProperties;
import com.blog.demo.common.exception.FileStorageException;

/**
 * Stores and deletes files on the local file system (BR09.2). Returns both the
 * on-disk path and the public URL path.
 */
@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final UploadProperties uploadProperties;
    private final Path rootDir;

    public FileStorageService(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
        this.rootDir = Paths.get(uploadProperties.dir()).toAbsolutePath().normalize();
    }

    /** Result of storing a file. */
    public record StoredFile(String storedFileName, String publicPath, String storagePath) {
    }

    public StoredFile store(MultipartFile file, String subDir, String extension) {
        try {
            Path targetDir = rootDir.resolve(subDir).normalize();
            Files.createDirectories(targetDir);

            String storedFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
            Path target = targetDir.resolve(storedFileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            String publicPath = uploadProperties.publicBasePath() + "/" + subDir + "/" + storedFileName;
            return new StoredFile(storedFileName, publicPath, target.toString());
        } catch (IOException e) {
            throw new FileStorageException("Không thể lưu tệp tải lên");
        }
    }

    public void delete(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(Paths.get(storagePath));
        } catch (IOException e) {
            // Non-fatal: log and continue so DB cleanup is not blocked.
            log.warn("Failed to delete file {}: {}", storagePath, e.getMessage());
        }
    }
}
