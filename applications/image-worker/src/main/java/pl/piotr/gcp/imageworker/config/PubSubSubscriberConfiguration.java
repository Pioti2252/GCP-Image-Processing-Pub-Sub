package pl.piotr.gcp.imageworker.config;

import java.io.IOException;

import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PubSubSubscriberConfiguration {

    @Bean(destroyMethod = "stopAsync")
    Subscriber imageJobSubscriber(
            MessageReceiver messageReceiver,
            @Value("${app.pubsub.project-id}")
            String projectId,
            @Value("${app.pubsub.subscription-id}")
            String subscriptionId,
            @Value("${app.pubsub.emulator-host:}")
            String emulatorHost
    ) throws IOException {

        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(
                        projectId,
                        subscriptionId
                );

        Subscriber.Builder builder = Subscriber.newBuilder(
                subscriptionName,
                messageReceiver
        );

        if (
                emulatorHost != null
                        && !emulatorHost.isBlank()
        ) {
            ManagedChannel channel = ManagedChannelBuilder
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