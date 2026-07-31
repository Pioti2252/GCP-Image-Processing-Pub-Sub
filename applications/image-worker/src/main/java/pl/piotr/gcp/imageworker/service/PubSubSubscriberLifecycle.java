package pl.piotr.gcp.imageworker.service;

import com.google.cloud.pubsub.v1.Subscriber;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PubSubSubscriberLifecycle {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PubSubSubscriberLifecycle.class);

    private final Subscriber subscriber;

    public PubSubSubscriberLifecycle(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    @PostConstruct
    public void start() {
        subscriber.startAsync().awaitRunning();

        LOGGER.info(
                "Pub/Sub subscriber started: subscription={}",
                subscriber.getSubscriptionNameString()
        );
    }

    @PreDestroy
    public void stop() {
        subscriber.stopAsync().awaitTerminated();

        LOGGER.info("Pub/Sub subscriber stopped");
    }
}