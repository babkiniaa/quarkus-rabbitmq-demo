package org.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.dto.enums.NotificationType;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ApplicationScoped
public class RoutingKeyManager {

    private static final String ROUTING_KEY_PREFIX = "notification.";
    private static final Map<NotificationType, String> ROUTING_KEY_MAP = new HashMap<>();

    static {
        ROUTING_KEY_MAP.put(NotificationType.EMAIL, "email");
        ROUTING_KEY_MAP.put(NotificationType.TELEGRAM, "telegram");
        ROUTING_KEY_MAP.put(NotificationType.SMS, "sms");
        ROUTING_KEY_MAP.put(NotificationType.ALL, "all");
    }

    public String getRoutingKey(@NotNull(message = "Type cannot be null") NotificationType type) {
        String key = ROUTING_KEY_MAP.get(type);
        if (key == null) {
            log.warn("Неизвестный тип уведомления: %s, используется 'default'", type);
            key = "default";
        }
        return ROUTING_KEY_PREFIX + key;
    }

    public NotificationType getTypeFromRoutingKey(String routingKey) {
        if (routingKey == null || !routingKey.startsWith(ROUTING_KEY_PREFIX)) {
            return null;
        }

        String key = routingKey.substring(ROUTING_KEY_PREFIX.length());
        return ROUTING_KEY_MAP.entrySet().stream()
                .filter(entry -> entry.getValue().equals(key))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public Map<NotificationType, String> getAllRoutingKeys() {
        Map<NotificationType, String> result = new HashMap<>();
        ROUTING_KEY_MAP.forEach((type, key) ->
                result.put(type, ROUTING_KEY_PREFIX + key));
        return result;
    }

    public boolean isValidRoutingKey(String routingKey) {
        if (routingKey == null) return false;
        return getAllRoutingKeys().containsValue(routingKey) ||
                routingKey.equals(ROUTING_KEY_PREFIX + "default");
    }
}