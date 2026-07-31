package pl.piotr.gcp.imageapi.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import pl.piotr.gcp.imageapi.domain.OutboxEvent;
import pl.piotr.gcp.imageapi.domain.OutboxEventStatus;
import pl.piotr.gcp.imageapi.repository.OutboxEventRepository;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import pl.piotr.gcp.imageapi.domain.ImageJob;
import pl.piotr.gcp.imageapi.domain.ImageJobStatus;
import pl.piotr.gcp.imageapi.dto.ImageJobMessage;
import pl.piotr.gcp.imageapi.exception.InvalidImageException;
import pl.piotr.gcp.imageapi.repository.ImageJobRepository;

@Service
public class ImageJobService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final ImageJobRepository imageJobRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final JsonMapper jsonMapper;
    private final Path uploadDirectory;

    public ImageJobService(
            ImageJobRepository imageJobRepository,
            OutboxEventRepository outboxEventRepository,
            JsonMapper jsonMapper,
            @Value("${app.storage.upload-directory:uploads}")
            String uploadDirectory
    ) {
        this.imageJobRepository = imageJobRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.jsonMapper = jsonMapper;

        this.uploadDirectory = Path.of(uploadDirectory)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.uploadDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Nie udało się utworzyć katalogu na pliki",
                    exception
            );
        }
    }

    @Transactional
    public ImageJob createJob(MultipartFile file) {
        validateFile(file);

        UUID jobId = UUID.randomUUID();

        String storedFilename =
                jobId + resolveExtension(file.getContentType());

        Path targetPath = uploadDirectory
                .resolve(storedFilename)
                .normalize();

        if (!targetPath.startsWith(uploadDirectory)) {
            throw new InvalidImageException(
                    "Nieprawidłowa ścieżka pliku"
            );
        }

        try {
            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Nie udało się zapisać przesłanego pliku",
                    exception
            );
        }

        Instant now = Instant.now();

        ImageJob imageJob = new ImageJob(
                jobId,
                file.getOriginalFilename(),
                storedFilename,
                file.getContentType(),
                file.getSize(),
                ImageJobStatus.PENDING,
                now,
                now
        );

        ImageJob savedJob = imageJobRepository.save(imageJob);

        String correlationId = UUID.randomUUID().toString();

        ImageJobMessage message = new ImageJobMessage(
                savedJob.getId(),
                savedJob.getStoredFilename(),
                correlationId,
                Instant.now()
        );

        String payload = jsonMapper.writeValueAsString(message);

        OutboxEvent outboxEvent = new OutboxEvent(
                UUID.randomUUID(),
                savedJob.getId(),
                "IMAGE_JOB",
                "IMAGE_JOB_CREATED",
                payload,
                OutboxEventStatus.PENDING,
                0,
                Instant.now()
        );

        outboxEventRepository.save(outboxEvent);

        return savedJob;
    }

    @Transactional(readOnly = true)
    public Optional<ImageJob> findById(UUID id) {
        return imageJobRepository.findById(id);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException(
                    "Plik nie może być pusty"
            );
        }

        String contentType = file.getContentType();

        if (
                contentType == null
                        || !ALLOWED_CONTENT_TYPES.contains(contentType)
        ) {
            throw new InvalidImageException(
                    "Dozwolone formaty plików: JPEG, PNG i WEBP"
            );
        }
    }

    private String resolveExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new InvalidImageException(
                    "Nieobsługiwany format obrazu"
            );
        };
    }
}