package pl.piotr.gcp.imageapi.service;

import java.io.IOException;
import java.util.UUID;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudImageStorage {

    private final Storage storage;
    private final String bucketName;
    private final String uploadPrefix;

    public CloudImageStorage(
            Storage storage,
            @Value("${app.storage.bucket-name}") String bucketName,
            @Value("${app.storage.upload-prefix:uploads/}") String uploadPrefix
    ) {
        this.storage = storage;
        this.bucketName = bucketName;
        this.uploadPrefix = normalizePrefix(uploadPrefix);
    }

    public String upload(
            UUID jobId,
            String extension,
            String contentType,
            MultipartFile file
    ) {
        String objectName = uploadPrefix + jobId + extension;

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .setContentType(contentType)
                .build();

        try {
            storage.create(blobInfo, file.getBytes());
            return objectName;
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Nie udało się zapisać pliku w Cloud Storage",
                    exception
            );
        }
    }

    private String normalizePrefix(String prefix) {
        return prefix.endsWith("/") ? prefix : prefix + "/";
    }
}