package com.cirms.service.impl;

import com.cirms.exception.BadRequestException;
import com.cirms.exception.ResourceNotFoundException;
import com.cirms.service.FileStorageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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
import java.util.Map;
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

    @Value("${CLOUDINARY_URL:}")
    private String cloudinaryUrl;

    private Path rootLocation;
    private Cloudinary cloudinary;
    private boolean useCloudinary = false;

    @PostConstruct
    public void init() {
        // Initialize Cloudinary if URL is configured
        if (cloudinaryUrl != null && !cloudinaryUrl.isBlank()) {
            try {
                this.cloudinary = new Cloudinary(cloudinaryUrl);
                this.useCloudinary = true;
                log.info("Cloudinary storage initialized successfully.");
            } catch (Exception e) {
                log.warn("Failed to initialize Cloudinary, falling back to local storage: {}", e.getMessage());
            }
        }

        // Always initialize local storage as fallback
        try {
            this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(this.rootLocation);
            if (!useCloudinary) {
                log.info("Using local file upload directory: {}", this.rootLocation);
            }
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

        if (useCloudinary) {
            return storeToCloudinary(file);
        } else {
            return storeToLocal(file, extension);
        }
    }

    @SuppressWarnings("unchecked")
    private String storeToCloudinary(MultipartFile file) {
        try {
            String publicId = "cirms/" + UUID.randomUUID();
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "overwrite", true,
                            "resource_type", "image"
                    )
            );
            String url = (String) uploadResult.get("secure_url");
            log.info("Successfully uploaded image to Cloudinary: {}", url);
            return url;
        } catch (IOException e) {
            log.error("Failed to upload file to Cloudinary", e);
            throw new BadRequestException("Failed to upload image to cloud storage: " + e.getMessage());
        }
    }

    private String storeToLocal(MultipartFile file, String extension) {
        String safeFilename = UUID.randomUUID().toString() + "." + extension.toLowerCase(Locale.ROOT);
        try {
            Path destinationFile = this.rootLocation.resolve(safeFilename).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation)) {
                throw new BadRequestException("Cannot store file outside current directory");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            log.info("Successfully stored uploaded image locally as: {}", safeFilename);
            return "/api/items/images/" + safeFilename;
        } catch (IOException e) {
            log.error("Failed to store file locally: {}", safeFilename, e);
            throw new BadRequestException("Failed to store uploaded file: " + e.getMessage());
        }
    }

    @Override
    public Resource loadFileAsResource(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new ResourceNotFoundException("Filename cannot be empty");
        }

        // If it's a full URL (Cloudinary), redirect-style loading isn't supported here —
        // the frontend should use the URL directly
        if (filename.startsWith("http://") || filename.startsWith("https://")) {
            try {
                Resource resource = new UrlResource(filename);
                if (resource.exists()) {
                    return resource;
                }
            } catch (MalformedURLException e) {
                throw new ResourceNotFoundException("Image URL not accessible: " + filename);
            }
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

        // If it's a Cloudinary URL, extract public_id and delete from Cloudinary
        if (useCloudinary && (fileUrlOrName.startsWith("http://") || fileUrlOrName.startsWith("https://"))) {
            deleteFromCloudinary(fileUrlOrName);
            return;
        }

        // Local file deletion
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

    private void deleteFromCloudinary(String secureUrl) {
        try {
            // Extract public_id from URL: .../upload/v.../cirms/uuid.ext -> cirms/uuid
            String publicId = extractPublicIdFromUrl(secureUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Deleted Cloudinary image with public_id: {}", publicId);
            }
        } catch (IOException e) {
            log.warn("Could not delete image from Cloudinary: {}", secureUrl, e);
        }
    }

    private String extractPublicIdFromUrl(String url) {
        try {
            // Cloudinary URL format: https://res.cloudinary.com/{cloud}/image/upload/v{version}/{public_id}.{ext}
            int uploadIdx = url.indexOf("/upload/");
            if (uploadIdx == -1) return null;
            String afterUpload = url.substring(uploadIdx + "/upload/".length());
            // Strip version prefix (v1234567/)
            if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
                afterUpload = afterUpload.substring(afterUpload.indexOf('/') + 1);
            }
            // Strip extension
            int dotIdx = afterUpload.lastIndexOf('.');
            if (dotIdx != -1) {
                afterUpload = afterUpload.substring(0, dotIdx);
            }
            return afterUpload;
        } catch (Exception e) {
            log.warn("Could not extract public_id from Cloudinary URL: {}", url);
            return null;
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
