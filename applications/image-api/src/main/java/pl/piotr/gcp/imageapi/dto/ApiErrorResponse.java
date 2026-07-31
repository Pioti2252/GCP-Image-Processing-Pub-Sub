package pl.piotr.gcp.imageapi.dto;

import java.time.Instant;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp
) {
}