package org.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.dto.NotificationRequest;
import org.dto.NotificationResponse;
import org.dto.enums.NotificationType;
import org.jboss.logging.Logger;
import org.producer.NotificationProducer;
import org.validation.ValidNotification;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@ApplicationScoped
public class NotificationService {

    @Inject
    NotificationProducer notificationProducer;
    @Inject
    ValidNotification validationService;

    public NotificationResponse processNotification(NotificationRequest request) {
        log.info("Обработка уведомления: %s", request.getId());

        validationService.validate(request);
        enrichRequest(request);

        notificationProducer.sendWithMetadata(request);

        NotificationResponse response = new NotificationResponse();
        response.setId(request.getId());
        response.setType(request.getType());
        response.setRecipient(request.getRecipient());
        response.setStatus("SENT_TO_QUEUE");
        response.setMessage("Уведомление отправлено в RabbitMQ");
        response.setTimestamp(java.time.LocalDateTime.now());
        response.setRoutingKey(request.getRoutingKey());
        response.setProcessedAt(java.time.LocalDateTime.now());

        return response;
    }

    private void enrichRequest(NotificationRequest request) {
        if (request.getId() == null) {
            request.setId(java.util.UUID.randomUUID().toString());
        }
        if (request.getTimestamp() == null) {
            request.setTimestamp(java.time.LocalDateTime.now());
        }
    }
}