package pl.piotr.gcp.imageapi.dto;

import java.time.Instant;
import java.util.UUID;

public record ImageJobMessage(
        UUID jobId,
        String storedFilename,
        String correlationId,
        Instant createdAt
) {
}