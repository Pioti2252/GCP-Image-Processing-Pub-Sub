package pl.piotr.gcp.imageapi.service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import pl.piotr.gcp.imageapi.domain.ImageJob;
import pl.piotr.gcp.imageapi.domain.ImageJobStatus;
import pl.piotr.gcp.imageapi.domain.OutboxEvent;
import pl.piotr.gcp.imageapi.domain.OutboxEventStatus;
import pl.piotr.gcp.imageapi.dto.ImageJobMessage;
import pl.piotr.gcp.imageapi.exception.InvalidImageException;
import pl.piotr.gcp.imageapi.repository.ImageJobRepository;
import pl.piotr.gcp.imageapi.repository.OutboxEventRepository;
import tools.jackson.databind.json.JsonMapper;

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
    private final CloudImageStorage cloudImageStorage;

    public ImageJobService(
            ImageJobRepository imageJobRepository,
            OutboxEventRepository outboxEventRepository,
            JsonMapper jsonMapper,
            CloudImageStorage cloudImageStorage
    ) {
        this.imageJobRepository = imageJobRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.jsonMapper = jsonMapper;
        this.cloudImageStorage = cloudImageStorage;
    }

    @Transactional
    public ImageJob createJob(MultipartFile file) {
        validateFile(file);

        UUID jobId = UUID.randomUUID();
        Instant now = Instant.now();

        String extension = resolveExtension(file.getContentType());

        String storedFilename = cloudImageStorage.upload(
                jobId,
                extension,
                file.getContentType(),
                file
        );

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
                now
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
                now
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