package pl.piotr.gcp.imageapi.service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tools.jackson.databind.json.JsonMapper;

@Service
public class ImageJobPublisher {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ImageJobPublisher.class);

    private final Publisher publisher;

    public ImageJobPublisher(
            Publisher publisher) {

        this.publisher = publisher;
    }

    public String publish(
            String payload,
            UUID aggregateId,
            String eventType
    ) {
        try {
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                    .setData(ByteString.copyFromUtf8(payload))
                    .putAttributes(
                            "aggregateId",
                            aggregateId.toString()
                    )
                    .putAttributes(
                            "eventType",
                            eventType
                    )
                    .build();

            String messageId = publisher
                    .publish(pubsubMessage)
                    .get(10, TimeUnit.SECONDS);

            LOGGER.info(
                    "Published outbox event: aggregateId={}, eventType={}, messageId={}",
                    aggregateId,
                    eventType,
                    messageId
            );

            return messageId;

        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Publikowanie wiadomości zostało przerwane",
                    exception
            );

        } catch (TimeoutException exception) {
            throw new IllegalStateException(
                    "Przekroczono czas publikowania wiadomości",
                    exception
            );

        } catch (ExecutionException exception) {
            throw new IllegalStateException(
                    "Nie udało się opublikować wiadomości",
                    exception.getCause()
            );
        }
    }
}