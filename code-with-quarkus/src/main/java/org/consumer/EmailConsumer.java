package org.consumer;

import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.dto.NotificationRequest;

import java.util.concurrent.CompletionStage;

@Slf4j
@ApplicationScoped
public class EmailConsumer {

    @Incoming("email-in")
    @Acknowledgment(Acknowledgment.Strategy.MANUAL)
    @Blocking
    public CompletionStage<Void> processEmail(Message<NotificationRequest> message) {
        NotificationRequest notification = message.getPayload();

        try {
            log.info("Processing email notification: {}", notification.getId());
            log.info("Recipient: {}", notification.getRecipient());
            log.info("Subject: {}", notification.getSubject());
            log.info("Message: {}", notification.getMessage());

            Thread.sleep(1000);

            log.info("Email sent successfully: {}", notification.getId());

            return message.ack();

        } catch (Exception e) {
            log.error("Failed to send email {}: {}", notification.getId(), e.getMessage());
            return message.nack(e);
        }
    }
}
