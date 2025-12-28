package org.dto;

import lombok.Getter;
import lombok.Setter;
import org.dto.enums.NotificationType;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponse {

    private String id;
    private NotificationType type;
    private String recipient;
    private String status;
    private String message;
    private LocalDateTime timestamp;
    private LocalDateTime processedAt;
    private String queueName;
    private String routingKey;

}
