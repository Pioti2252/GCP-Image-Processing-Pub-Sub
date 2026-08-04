package pl.piotr.gcp.imageapi.config;

import java.io.IOException;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PubSubConfiguration {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PubSubConfiguration.class);

    @Bean(destroyMethod = "shutdown")
    public Publisher pubSubPublisher(
            @Value("${app.pubsub.project-id}")
            String projectId,

            @Value("${app.pubsub.topic-id}")
            String topicId,

            @Value("${app.pubsub.emulator-host:}")
            String emulatorHost
    ) throws IOException {

        TopicName topicName = TopicName.of(projectId, topicId);

        LOGGER.info(
                "Creating Pub/Sub Publisher: topic={}, mode={}, emulatorHost='{}'",
                topicName,
                emulatorHost == null || emulatorHost.isBlank()
                        ? "GCP"
                        : "EMULATOR",
                emulatorHost
        );

        Publisher.Builder builder =
                Publisher.newBuilder(topicName);

        if (emulatorHost != null && !emulatorHost.isBlank()) {
            ManagedChannel channel =
                    ManagedChannelBuilder
                            .forTarget(emulatorHost)
                            .usePlaintext()
                            .build();

            builder.setChannelProvider(
                    FixedTransportChannelProvider.create(
                            GrpcTransportChannel.create(channel)
                    )
            );

            builder.setCredentialsProvider(
                    NoCredentialsProvider.create()
            );
        }

        return builder.build();
    }
}