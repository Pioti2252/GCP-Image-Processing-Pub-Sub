package pl.piotr.gcp.imageworker.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.coobird.thumbnailator.Thumbnails;

import org.springframework.stereotype.Service;

import pl.piotr.gcp.imageworker.domain.ImageJob;

@Service
public class ImageProcessor {

    private final CloudImageStorage cloudImageStorage;

    public ImageProcessor(
            CloudImageStorage cloudImageStorage
    ) {
        this.cloudImageStorage = cloudImageStorage;
    }

    public void process(ImageJob imageJob) throws IOException {
        Path sourcePath = null;
        Path outputPath = null;

        try {
            sourcePath = cloudImageStorage.downloadToTemporaryFile(
                    imageJob.getStoredFilename()
            );

            String extension = resolveExtension(
                    imageJob.getStoredFilename()
            );

            outputPath = Files.createTempFile(
                    "thumbnail-" + imageJob.getId() + "-",
                    extension
            );

            Thumbnails.of(sourcePath.toFile())
                    .size(400, 400)
                    .keepAspectRatio(true)
                    .toFile(outputPath.toFile());

            String processedObjectName =
                    cloudImageStorage.uploadProcessed(
                            outputPath,
                            imageJob.getId().toString(),
                            extension,
                            imageJob.getContentType()
                    );

            // Na razie tylko wysyłamy wynik do GCS.
            // Później zapiszemy processedObjectName w bazie.
        } finally {
            deleteTemporaryFile(sourcePath);
            deleteTemporaryFile(outputPath);
        }
    }

    private String resolveExtension(String objectName) {
        int dotIndex = objectName.lastIndexOf('.');

        if (dotIndex < 0) {
            throw new IllegalArgumentException(
                    "Nie można ustalić rozszerzenia obiektu: "
                            + objectName
            );
        }

        return objectName.substring(dotIndex);
    }

    private void deleteTemporaryFile(Path path) {
        if (path == null) {
            return;
        }

        try {
            Files.deleteIfExists(path);
        } catch (IOException exception) {
            // Nie przerywamy całego przetwarzania przez problem
            // z usunięciem pliku tymczasowego.
        }
    }
}