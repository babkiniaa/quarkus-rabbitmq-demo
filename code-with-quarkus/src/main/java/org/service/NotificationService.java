package org.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.dto.NotificationRequest;
import org.dto.NotificationResponse;
import org.dto.enums.NotificationType;
import org.jboss.logging.Logger;
import org.validation.ValidNotification;

import java.time.LocalDateTime;
import java.util.Map;

@ApplicationScoped
public class NotificationService {

    private static final Logger LOG = Logger.getLogger(NotificationService.class);
    @Inject
    ValidNotification validationService;

    public NotificationResponse processNotification(NotificationRequest request) {
        LOG.infof("Обработка уведомления: %s", request.getId());

        validationService.validate(request);

        LOG.infof("Уведомление %s прошло валидацию", request.getId());

        NotificationResponse response = new NotificationResponse();
        response.setId(request.getId());
        response.setType(request.getType());
        response.setRecipient(request.getRecipient());
        response.setStatus("ACCEPTED");
        response.setMessage("Уведомление принято в обработку");
        response.setTimestamp(LocalDateTime.now());

        LOG.infof("Уведомление %s обработано", request.getId());

        return response;
    }

    public Map<String, Object> createTestResponse() {
        LOG.info("Создание тестового ответа");

        NotificationRequest testRequest = new NotificationRequest();
        testRequest.setType(NotificationType.EMAIL);
        testRequest.setRecipient("test@example.com");
        testRequest.setSubject("Тестовое уведомление");
        testRequest.setMessage("Это тестовое уведомление");

        Map<String, Object> response = Map.of(
                "status", "success",
                "service", "BunnyNotifier",
                "version", "1.0.0",
                "timestamp", LocalDateTime.now(),
                "test_request", testRequest,
                "message", "Сервис работает"
        );

        return response;
    }

    public Map<String, Object> checkHealth() {
        return Map.of(
                "status", "UP",
                "service", "bunny-notifier",
                "timestamp", LocalDateTime.now(),
                "version", "1.0.0",
                "validation", "ENABLED"
        );
    }

    public Map<String, Object> getNotificationTypes() {
        Map<String, String> types = Map.of(
                "EMAIL", "Отправка по email",
                "TELEGRAM", "Отправка в Telegram",
                "SMS", "Отправка SMS",
                "ALL", "Отправка через все каналы"
        );

        return Map.of(
                "types", types,
                "count", 4,
                "default", "EMAIL"
        );
    }
}
