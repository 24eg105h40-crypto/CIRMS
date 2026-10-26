package com.cirms.service.impl;

import com.cirms.exception.BadRequestException;
import com.cirms.exception.ResourceNotFoundException;
import com.cirms.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5 MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp"
    );

    @Value("${cirms.upload.dir:${FILE_UPLOAD_LOCATION:./uploads}}")
    private String uploadDir;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(this.rootLocation);
            log.info("Initialized file upload directory at: {}", this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage folder: " + uploadDir, e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Validate file size (5MB max)
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BadRequestException("Image file size exceeds the maximum limit of 5 MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BadRequestException("Uploaded file must have a valid filename");
        }

        // Prevent path traversal
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new BadRequestException("Filename contains invalid path characters: " + originalFilename);
        }

        // Validate extension
        String extension = getFileExtension(originalFilename);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT))) {
            throw new BadRequestException("Invalid image file format. Supported formats: JPG, JPEG, PNG, WEBP");
        }

        // Validate MIME type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BadRequestException("Invalid image MIME type: " + contentType + ". Supported types: image/jpeg, image/png, image/webp");
        }

        // Generate safe unique filename
        String safeFilename = UUID.randomUUID().toString() + "." + extension.toLowerCase(Locale.ROOT);

        try {
            Path destinationFile = this.rootLocation.resolve(safeFilename).normalize().toAbsolutePath();

            // Security check: ensure file is inside the root directory
            if (!destinationFile.getParent().equals(this.rootLocation)) {
                throw new BadRequestException("Cannot store file outside current directory");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("Successfully stored uploaded image as: {}", safeFilename);
            return "/api/items/images/" + safeFilename;
        } catch (IOException e) {
            log.error("Failed to store file: {}", originalFilename, e);
            throw new BadRequestException("Failed to store uploaded file: " + e.getMessage());
        }
    }

    @Override
    public Resource loadFileAsResource(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new ResourceNotFoundException("Filename cannot be empty");
        }

        // Prevent path traversal
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new BadRequestException("Invalid filename characters");
        }

        try {
            Path file = this.rootLocation.resolve(filename).normalize();
            if (!file.startsWith(this.rootLocation)) {
                throw new BadRequestException("Access denied to file outside upload directory");
            }

            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Image file not found: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Image file not found: " + filename);
        }
    }

    @Override
    public void deleteFile(String fileUrlOrName) {
        if (fileUrlOrName == null || fileUrlOrName.isBlank()) {
            return;
        }

        try {
            String filename = fileUrlOrName;
            if (filename.contains("/api/items/images/")) {
                filename = filename.substring(filename.lastIndexOf("/api/items/images/") + "/api/items/images/".length());
            } else if (filename.contains("/uploads/")) {
                filename = filename.substring(filename.lastIndexOf("/uploads/") + "/uploads/".length());
            } else if (filename.contains("/")) {
                filename = filename.substring(filename.lastIndexOf('/') + 1);
            } else if (filename.contains("\\")) {
                filename = filename.substring(filename.lastIndexOf('\\') + 1);
            }

            // Sanitize
            if (filename.contains("..")) {
                log.warn("Blocked directory traversal attempt during delete: {}", fileUrlOrName);
                return;
            }

            Path filePath = this.rootLocation.resolve(filename).normalize();
            if (!filePath.startsWith(this.rootLocation)) {
                log.warn("Blocked deletion of file outside upload directory: {}", filePath);
                return;
            }

            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("Deleted image file: {}", filePath);
            }
        } catch (IOException e) {
            log.warn("Could not delete image file: {}", fileUrlOrName, e);
        }
    }

    private String getFileExtension(String filename) {
        int lastIndex = filename.lastIndexOf('.');
        if (lastIndex == -1 || lastIndex == filename.length() - 1) {
            return null;
        }
        return filename.substring(lastIndex + 1);
    }
}
