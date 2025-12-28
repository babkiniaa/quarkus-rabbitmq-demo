package org.consumer;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.dto.NotificationRequest;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@Slf4j
@ApplicationScoped
public class SmsConsumer {

    private int deliveryAttempts = 0;

    @Incoming("sms-in")
    public void processSms(NotificationRequest notification) {
        log.info("Processing SMS notification: {}", notification.getId());
        log.info("Phone: {}", notification.getRecipient());
        log.info("Message: {}", notification.getMessage());

        deliveryAttempts++;

        if (notification.isUrgent() && deliveryAttempts < 3) {
            log.warn("SMS gateway busy, attempt: {}", deliveryAttempts);
            throw new RuntimeException("SMS gateway unavailable");
        }

        deliveryAttempts = 0;
        log.info("SMS delivered: {}", notification.getId());
    }
}