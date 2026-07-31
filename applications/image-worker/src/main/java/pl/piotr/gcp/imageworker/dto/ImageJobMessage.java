package pl.piotr.gcp.imageworker.dto;

import java.time.Instant;
import java.util.UUID;

public record ImageJobMessage(
        UUID jobId,
        String storedFilename,
        String correlationId,
        Instant createdAt
) {
}