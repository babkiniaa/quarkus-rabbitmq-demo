package org.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.dto.NotificationRequest;
import org.dto.enums.NotificationType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.service.CorrelationIdManager;
import org.service.RoutingKeyManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class RabbitMQMetadataFactory {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Inject
    RoutingKeyManager routingKeyManager;

    @Inject
    CorrelationIdManager correlationIdManager;

    @ConfigProperty(name = "message.ttl.email", defaultValue = "86400000")
    long emailTtl;

    @ConfigProperty(name = "message.ttl.telegram", defaultValue = "3600000")
    long telegramTtl;

    @ConfigProperty(name = "message.ttl.sms", defaultValue = "300000")
    long smsTtl;

    @ConfigProperty(name = "message.ttl.default", defaultValue = "3600000")
    long defaultTtl;

    public void enrichWithMetadata(NotificationRequest request) {
        log.debug("Enriching metadata for request");

        if (request.getRoutingKey() == null) {
            String routingKey = routingKeyManager.getRoutingKey(request.getType());
            request.setRoutingKey(routingKey);
            log.debug("Set routing key: {}", routingKey);
        }

        if (request.getCorrelationId() == null) {
            String correlationId = correlationIdManager.generateCorrelationId();
            request.setCorrelationId(correlationId);
            log.debug("Set correlation ID: {}", correlationId);
        }

        if (request.getTtl() == null) {
            long ttl = getMessageTtl(request.getType());
            request.setTtl(ttl);
            log.debug("Set TTL: {} ms", ttl);
        }

        if (request.getId() == null || request.getId().isEmpty()) {
            request.setId(generateMessageId(request.getType()));
            log.debug("Generated message ID: {}", request.getId());
        }

        if (request.getHeaders() == null) {
            request.setHeaders(new HashMap<>());
        }

        Map<String, Object> systemHeaders = createSystemHeaders(request);
        request.getHeaders().putAll(systemHeaders);

        log.info("Request enriched with metadata. ID: {}", request.getId());
    }

    private Map<String, Object> createSystemHeaders(NotificationRequest request) {
        Map<String, Object> headers = new HashMap<>();

        headers.put("x-service", "bunny-notifier");
        headers.put("x-version", "1.0.0");
        headers.put("x-timestamp", LocalDateTime.now().toString());
        headers.put("x-message-id", request.getId());
        headers.put("x-correlation-id", request.getCorrelationId());
        headers.put("x-routing-key", request.getRoutingKey());
        headers.put("x-ttl-ms", request.getTtl());
        headers.put("x-type", request.getType().name());
        headers.put("x-priority", request.getPriority());
        headers.put("recipient", request.getRecipient());

        if (request.getSubject() != null) {
            headers.put("subject", request.getSubject());
        }

        if (request.getTraceId() != null) {
            headers.put("x-trace-id", request.getTraceId());
        } else {
            headers.put("x-trace-id", UUID.randomUUID().toString().substring(0, 8));
        }

        return headers;
    }

    public long getMessageTtl(NotificationType type) {
        return switch (type) {
            case EMAIL -> emailTtl;
            case TELEGRAM -> telegramTtl;
            case SMS -> smsTtl;
            case ALL -> defaultTtl;
        };
    }

    private String generateMessageId(NotificationType type) {
        return String.format("%s-%s-%s",
                type.name().toLowerCase(),
                LocalDateTime.now().format(DATE_FORMATTER),
                UUID.randomUUID().toString().substring(0, 8)
        );
    }

    public boolean validateMetadata(NotificationRequest request) {
        boolean valid = true;

        if (request.getId() == null) {
            log.warn("Message ID is not set");
            valid = false;
        }

        if (!routingKeyManager.isValidRoutingKey(request.getRoutingKey())) {
            log.warn("Invalid routing key: {}", request.getRoutingKey());
            valid = false;
        }

        if (!correlationIdManager.isValidCorrelationId(request.getCorrelationId())) {
            log.warn("Invalid correlation ID: {}", request.getCorrelationId());
            valid = false;
        }

        return valid;
    }
}