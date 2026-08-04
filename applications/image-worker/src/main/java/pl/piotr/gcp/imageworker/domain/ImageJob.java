package pl.piotr.gcp.imageworker.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "image_jobs")
public class ImageJob {

    @Id
    private UUID id;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "stored_filename", nullable = false)
    private String storedFilename;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageJobStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "processing_attempts", nullable = false)
    private int processingAttempts;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "failed_at")
    private Instant failedAt;

    protected ImageJob() {
    }

    public UUID getId() {
        return id;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public ImageJobStatus getStatus() {
        return status;
    }

    public int getProcessingAttempts() {
        return processingAttempts;
    }

    public String getLastError() {
        return lastError;
    }

    public Instant getFailedAt() {
        return failedAt;
    }

    public void markAsProcessing() {
        status = ImageJobStatus.PROCESSING;
        processingAttempts++;
        lastError = null;
        updatedAt = Instant.now();
    }

    public void markAsCompleted() {
        status = ImageJobStatus.COMPLETED;
        lastError = null;
        failedAt = null;
        updatedAt = Instant.now();
    }

    public void markForRetry(String error) {
        status = ImageJobStatus.PENDING;
        lastError = limitError(error);
        updatedAt = Instant.now();
    }

    public void markAsFailed(String error) {
        status = ImageJobStatus.FAILED;
        lastError = limitError(error);
        failedAt = Instant.now();
        updatedAt = Instant.now();
    }
    public String getContentType() {
        return contentType;
    }

    private String limitError(String error) {
        if (error == null) {
            return null;
        }

        return error.length() <= 4000
                ? error
                : error.substring(0, 4000);
    }
}