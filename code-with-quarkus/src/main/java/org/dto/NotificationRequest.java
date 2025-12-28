package org.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.dto.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class NotificationRequest {
    private String id;

    @NotNull(message = "Type cannot be null")
    private NotificationType type;

    @NotBlank(message = "Recipient cannot be blank")
    @Size(max = 255, message = "Recipient too long")
    private String recipient;

    @Size(max = 100, message = "Subject too long")
    private String subject;

    @NotBlank(message = "Message cannot be blank")
    @Size(max = 2000, message = "Message too long")
    private String message;

    private String priority = "NORMAL";
    private LocalDateTime timestamp;

    private String routingKey;
    private String correlationId;
    private Long ttl;
    private Map<String, Object> headers = new HashMap<>();

    private String businessId;
    private String category;

    private String traceId;
    private String source = "bunny-notifier";
    private boolean urgent = false;
    private boolean requireReceipt = false;
}