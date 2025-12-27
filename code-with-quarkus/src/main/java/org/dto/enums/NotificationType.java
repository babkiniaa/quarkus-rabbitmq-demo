package org.dto.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum NotificationType {
    EMAIL("Email notification"),
    TELEGRAM("Telegram message"),
    SMS("SMS text message"),
    ALL("Send to all channels");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }
}
