package pl.piotr.gcp.imageworker.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.coobird.thumbnailator.Thumbnails;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import pl.piotr.gcp.imageworker.domain.ImageJob;

@Service
public class ImageProcessor {

    private final Path uploadDirectory;
    private final Path processedDirectory;

    public ImageProcessor(
            @Value("${app.storage.upload-directory}")
            String uploadDirectory,
            @Value("${app.storage.processed-directory}")
            String processedDirectory
    ) {
        this.uploadDirectory = Path.of(uploadDirectory)
                .toAbsolutePath()
                .normalize();

        this.processedDirectory = Path.of(processedDirectory)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.processedDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Nie udało się utworzyć katalogu processed",
                    exception
            );
        }
    }

    public void process(ImageJob imageJob) throws IOException {
        Path sourcePath = uploadDirectory
                .resolve(imageJob.getStoredFilename())
                .normalize();

        if (!sourcePath.startsWith(uploadDirectory)) {
            throw new IOException("Nieprawidłowa ścieżka pliku wejściowego");
        }

        if (!Files.exists(sourcePath)) {
            throw new IOException(
                    "Nie znaleziono pliku: " + sourcePath
            );
        }

        String outputFilename =
                "thumbnail-" + imageJob.getStoredFilename();

        Path outputPath = processedDirectory
                .resolve(outputFilename)
                .normalize();

        if (!outputPath.startsWith(processedDirectory)) {
            throw new IOException("Nieprawidłowa ścieżka pliku wynikowego");
        }

        Thumbnails.of(sourcePath.toFile())
                .size(400, 400)
                .keepAspectRatio(true)
                .toFile(outputPath.toFile());
    }
}