package pl.piotr.gcp.imageworker.service;

import java.nio.file.Files;
import java.nio.file.Path;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CloudImageStorage {

    private final Storage storage;
    private final String bucketName;
    private final String processedPrefix;

    public CloudImageStorage(
            Storage storage,
            @Value("${app.storage.bucket-name}") String bucketName,
            @Value("${app.storage.processed-prefix:processed/}")
            String processedPrefix
    ) {
        this.storage = storage;
        this.bucketName = bucketName;
        this.processedPrefix = normalizePrefix(processedPrefix);
    }

    public Path downloadToTemporaryFile(String objectName) {
        Blob blob = storage.get(bucketName, objectName);

        if (blob == null) {
            throw new IllegalStateException(
                    "Nie znaleziono obiektu w Cloud Storage: " + objectName
            );
        }

        try {
            String suffix = extensionFrom(objectName);
            Path temporaryFile = Files.createTempFile(
                    "image-processing-",
                    suffix
            );

            blob.downloadTo(temporaryFile);
            return temporaryFile;

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Nie udało się pobrać obiektu z Cloud Storage",
                    exception
            );
        }
    }

    public String uploadProcessed(
            Path file,
            String jobId,
            String extension,
            String contentType
    ) {
        String objectName =
                processedPrefix + jobId + "-thumbnail" + extension;

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .setContentType(contentType)
                .build();

        try {
            storage.createFrom(blobInfo, file);
            return objectName;
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Nie udało się zapisać przetworzonego obrazu",
                    exception
            );
        }
    }

    private String extensionFrom(String objectName) {
        int dotIndex = objectName.lastIndexOf('.');
        return dotIndex >= 0 ? objectName.substring(dotIndex) : ".tmp";
    }

    private String normalizePrefix(String prefix) {
        return prefix.endsWith("/") ? prefix : prefix + "/";
    }
}