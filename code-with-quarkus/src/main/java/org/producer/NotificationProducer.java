package org.producer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.dto.NotificationRequest;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.factory.RabbitMQMetadataFactory;

import java.util.UUID;

@Slf4j
@ApplicationScoped
public class NotificationProducer {

    @Inject
    @Channel("notifications-out")
    @OnOverflow(value = OnOverflow.Strategy.BUFFER, bufferSize = 1000)
    Emitter<NotificationRequest> emitter;

    @Inject
    RabbitMQMetadataFactory metadataFactory;

    public void sendWithMetadata(NotificationRequest request) {
        try {
            metadataFactory.enrichWithMetadata(request);

            log.info("Sending to RabbitMQ. ID: {}, Routing: {}, Correlation: {}",
                    request.getId(),
                    request.getRoutingKey(),
                    request.getCorrelationId());

            emitter.send(request);

            log.info("Sent. ID: {}", request.getId());

        } catch (Exception e) {
            log.error("Error sending {}: {}", request.getId(), e.getMessage());
            throw e;
        }
    }

    private String generateMessageId(org.dto.enums.NotificationType type) {
        return String.format("%s-%s",
                type.name().toLowerCase(),
                UUID.randomUUID().toString().substring(0, 8)
        );
    }

    private String getRoutingKey(org.dto.enums.NotificationType type) {
        return switch (type) {
            case EMAIL -> "notification.email";
            case TELEGRAM -> "notification.telegram";
            case SMS -> "notification.sms";
            case ALL -> "notification.all";
        };
    }

    private String generateCorrelationId() {
        return "corr-" + UUID.randomUUID().toString();
    }
}