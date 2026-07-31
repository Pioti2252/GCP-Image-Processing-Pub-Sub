package pl.piotr.gcp.imageworker.service;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import pl.piotr.gcp.imageworker.dto.ImageJobMessage;
import tools.jackson.databind.json.JsonMapper;
import pl.piotr.gcp.imageworker.service.ProcessingResult;

@Component
public class ImageJobMessageReceiver implements MessageReceiver {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ImageJobMessageReceiver.class);

    private final JsonMapper jsonMapper;
    private final ImageJobWorker imageJobWorker;

    public ImageJobMessageReceiver(
            JsonMapper jsonMapper,
            ImageJobWorker imageJobWorker
    ) {
        this.jsonMapper = jsonMapper;
        this.imageJobWorker = imageJobWorker;
    }

    @Override
    public void receiveMessage(
            PubsubMessage pubsubMessage,
            AckReplyConsumer consumer
    ) {
        String payload = pubsubMessage
                .getData()
                .toStringUtf8();

        String messageId = pubsubMessage.getMessageId();

        try {
            ImageJobMessage message = jsonMapper.readValue(
                    payload,
                    ImageJobMessage.class
            );

            LOGGER.info(
                    "Pub/Sub message received: messageId={}, jobId={}, correlationId={}",
                    messageId,
                    message.jobId(),
                    message.correlationId()
            );

            ProcessingResult result = imageJobWorker.processJob(
                    message.jobId(),
                    message.correlationId()
            );

            switch (result) {
                case COMPLETED, ALREADY_COMPLETED -> {
                    consumer.ack();

                    LOGGER.info(
                            "Pub/Sub message acknowledged: messageId={}, jobId={}",
                            messageId,
                            message.jobId()
                    );
                }

                case RETRY -> {
                    consumer.nack();

                    LOGGER.warn(
                            "Pub/Sub message negatively acknowledged for retry: messageId={}, jobId={}",
                            messageId,
                            message.jobId()
                    );
                }

                case FAILED -> {
                    consumer.ack();

                    LOGGER.error(
                            "Pub/Sub message acknowledged after permanent failure: messageId={}, jobId={}",
                            messageId,
                            message.jobId()
                    );
                }
            }

        } catch (Exception exception) {
            LOGGER.error(
                    "Pub/Sub message processing failed: messageId={}, payload={}",
                    messageId,
                    payload,
                    exception
            );

            consumer.nack();
        }
    }
}