package pl.piotr.gcp.imageapi.config;

import java.io.IOException;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.cloud.pubsub.v1.Publisher;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.pubsub.v1.ProjectTopicName;

@Configuration
public class PubSubConfiguration {

    @Bean(name = "pubSubPublisher", destroyMethod = "shutdown")
    public Publisher pubSubPublisher(
            @Value("${app.pubsub.project-id}") String projectId,
            @Value("${app.pubsub.topic-id}") String topicId,
            @Value("${app.pubsub.emulator-host}") String emulatorHost
    ) throws IOException {

        ManagedChannel channel = ManagedChannelBuilder
                .forTarget(emulatorHost)
                .usePlaintext()
                .build();

        FixedTransportChannelProvider channelProvider =
                FixedTransportChannelProvider.create(
                        GrpcTransportChannel.create(channel)
                );

        return Publisher.newBuilder(
                        ProjectTopicName.of(projectId, topicId)
                )
                .setChannelProvider(channelProvider)
                .setCredentialsProvider(
                        NoCredentialsProvider.create()
                )
                .build();
    }
}