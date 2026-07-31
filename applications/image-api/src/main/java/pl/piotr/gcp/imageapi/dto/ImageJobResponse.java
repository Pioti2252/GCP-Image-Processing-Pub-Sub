package pl.piotr.gcp.imageapi.dto;

import java.time.Instant;
import java.util.UUID;

import pl.piotr.gcp.imageapi.domain.ImageJob;
import pl.piotr.gcp.imageapi.domain.ImageJobStatus;

public record ImageJobResponse(
        UUID id,
        String originalFilename,
        String contentType,
        long sizeBytes,
        ImageJobStatus status,
        Instant createdAt
) {

    public static ImageJobResponse from(ImageJob imageJob) {
        return new ImageJobResponse(
                imageJob.getId(),
                imageJob.getOriginalFilename(),
                imageJob.getContentType(),
                imageJob.getSizeBytes(),
                imageJob.getStatus(),
                imageJob.getCreatedAt()
        );
    }
}