package pl.piotr.gcp.imageworker.config;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
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
    public Subscriber imageJobSubscriber(
            @Value("${app.pubsub.project-id}")
            String projectId,

            @Value("${app.pubsub.subscription-id}")
            String subscriptionId,

            @Value("${app.pubsub.emulator-host}")
            String emulatorHost,

            MessageReceiver imageJobMessageReceiver
    ) {
        ManagedChannel channel = ManagedChannelBuilder
                .forTarget(emulatorHost)
                .usePlaintext()
                .build();

        FixedTransportChannelProvider channelProvider =
                FixedTransportChannelProvider.create(
                        GrpcTransportChannel.create(channel)
                );

        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(
                        projectId,
                        subscriptionId
                );

        return Subscriber.newBuilder(
                        subscriptionName,
                        imageJobMessageReceiver
                )
                .setChannelProvider(channelProvider)
                .setCredentialsProvider(
                        NoCredentialsProvider.create()
                )
                .build();
    }
}