package org.consumer;

import io.smallrye.common.annotation.NonBlocking;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.dto.NotificationRequest;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@Slf4j
@ApplicationScoped
public class TelegramConsumer {

    private int retryCount = 0;

    @Incoming("telegram-in")
    @NonBlocking
    public void processTelegram(NotificationRequest notification) {
        log.info("Processing telegram notification: {}", notification.getId());
        log.info("Chat ID: {}", notification.getRecipient());
        log.info("Message: {}", notification.getMessage());

        if (notification.getMessage().contains("error") && retryCount < 3) {
            retryCount++;
            throw new RuntimeException("Simulated telegram error, retry: " + retryCount);
        }

        retryCount = 0;
        log.info("Telegram message sent: {}", notification.getId());
    }
}
