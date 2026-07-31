package pl.piotr.gcp.imageapi.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    private UUID id;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxEventStatus status;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    protected OutboxEvent() {
    }

    public OutboxEvent(
            UUID id,
            UUID aggregateId,
            String aggregateType,
            String eventType,
            String payload,
            OutboxEventStatus status,
            int attempts,
            Instant createdAt
    ) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
        this.attempts = attempts;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public OutboxEventStatus getStatus() {
        return status;
    }

    public int getAttempts() {
        return attempts;
    }

    public void markAsProcessing() {
        status = OutboxEventStatus.PROCESSING;
        attempts++;
        lastError = null;
    }

    public void markAsPublished() {
        status = OutboxEventStatus.PUBLISHED;
        publishedAt = Instant.now();
        lastError = null;
    }

    public void markAsPending(String error) {
        status = OutboxEventStatus.PENDING;
        lastError = limitError(error);
    }

    public void markAsFailed(String error) {
        status = OutboxEventStatus.FAILED;
        lastError = limitError(error);
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