package com.fitpro.service;

import com.fitpro.dto.common.FileUploadResponse;
import com.fitpro.exception.BusinessException;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final MinioClient minioClient;
    private final String bucket;
    private final int signedUrlExpirySeconds;

    public FileStorageService(
            @Value("${fitpro.storage.endpoint}") String endpoint,
            @Value("${fitpro.storage.access-key}") String accessKey,
            @Value("${fitpro.storage.secret-key}") String secretKey,
            @Value("${fitpro.storage.bucket}") String bucket,
            @Value("${fitpro.storage.signed-url-expiry-seconds}") int signedUrlExpirySeconds) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        this.bucket = bucket;
        this.signedUrlExpirySeconds = signedUrlExpirySeconds;
    }

    public FileUploadResponse storeImage(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Image file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BusinessException("Only JPG, PNG and WEBP images are supported");
        }

        String extension = extensionFor(contentType);
        String fileName = UUID.randomUUID() + extension;
        String objectKey = folder + "/" + fileName;

        try {
            ensureBucket();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(contentType)
                    .build());
        } catch (IOException e) {
            throw new BusinessException("Image upload failed");
        } catch (Exception e) {
            throw new BusinessException("Image upload failed");
        }

        return FileUploadResponse.builder()
                .url(signedUrl(objectKey))
                .objectKey(objectKey)
                .fileName(fileName)
                .contentType(contentType)
                .size(file.getSize())
                .build();
    }

    public String signedUrl(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            return null;
        }
        if (objectKey.startsWith("http://") || objectKey.startsWith("https://")) {
            return objectKey;
        }
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(objectKey)
                    .expiry(signedUrlExpirySeconds, TimeUnit.SECONDS)
                    .build());
        } catch (Exception e) {
            throw new BusinessException("Signed image URL generation failed");
        }
    }

    private void ensureBucket() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}
