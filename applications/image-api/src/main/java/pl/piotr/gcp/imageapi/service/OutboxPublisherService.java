package pl.piotr.gcp.imageapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import pl.piotr.gcp.imageapi.domain.OutboxEvent;
import pl.piotr.gcp.imageapi.domain.OutboxEventStatus;
import pl.piotr.gcp.imageapi.repository.OutboxEventRepository;

@Service
public class OutboxPublisherService {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventProcessor outboxEventProcessor;
    private final int batchSize;

    public OutboxPublisherService(
            OutboxEventRepository outboxEventRepository,
            OutboxEventProcessor outboxEventProcessor,
            @Value("${app.outbox.batch-size:10}")
            int batchSize
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.outboxEventProcessor = outboxEventProcessor;
        this.batchSize = batchSize;
    }

    @Scheduled(
            fixedDelayString = "${app.outbox.polling-interval-ms:2000}"
    )
    public void publishPendingEvents() {
        List<OutboxEvent> events =
                outboxEventRepository.findByStatusOrderByCreatedAtAsc(
                        OutboxEventStatus.PENDING,
                        PageRequest.of(0, batchSize)
                );

        for (OutboxEvent event : events) {
            outboxEventProcessor.process(event.getId());
        }
    }
}