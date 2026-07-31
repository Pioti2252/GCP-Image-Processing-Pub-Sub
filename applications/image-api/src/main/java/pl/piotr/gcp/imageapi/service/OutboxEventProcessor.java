package pl.piotr.gcp.imageapi.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.piotr.gcp.imageapi.domain.OutboxEvent;
import pl.piotr.gcp.imageapi.domain.OutboxEventStatus;
import pl.piotr.gcp.imageapi.repository.OutboxEventRepository;

@Service
public class OutboxEventProcessor {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(OutboxEventProcessor.class);

    private final OutboxEventRepository outboxEventRepository;
    private final ImageJobPublisher imageJobPublisher;
    private final int maxAttempts;

    public OutboxEventProcessor(
            OutboxEventRepository outboxEventRepository,
            ImageJobPublisher imageJobPublisher,
            @Value("${app.outbox.max-attempts:5}")
            int maxAttempts
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.imageJobPublisher = imageJobPublisher;
        this.maxAttempts = maxAttempts;
    }

    @Transactional
    public void process(UUID eventId) {
        OutboxEvent event = outboxEventRepository.findById(eventId)
                .orElseThrow();

        if (event.getStatus() != OutboxEventStatus.PENDING) {
            return;
        }

        event.markAsProcessing();
        outboxEventRepository.saveAndFlush(event);

        try {
            imageJobPublisher.publish(
                    event.getPayload(),
                    event.getAggregateId(),
                    event.getEventType()
            );

            event.markAsPublished();
            outboxEventRepository.save(event);

            LOGGER.info(
                    "Outbox event published: eventId={}, aggregateId={}",
                    event.getId(),
                    event.getAggregateId()
            );

        } catch (Exception exception) {
            if (event.getAttempts() >= maxAttempts) {
                event.markAsFailed(exception.getMessage());
            } else {
                event.markAsPending(exception.getMessage());
            }

            outboxEventRepository.save(event);

            LOGGER.error(
                    "Outbox event publication failed: eventId={}, attempt={}",
                    event.getId(),
                    event.getAttempts(),
                    exception
            );
        }
    }
}