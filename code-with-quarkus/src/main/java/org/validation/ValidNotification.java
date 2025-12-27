package org.validation;

import lombok.extern.slf4j.Slf4j;
import org.dto.NotificationRequest;
import org.dto.enums.NotificationType;

import java.util.regex.Pattern;

@Slf4j
public class ValidNotification {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");

    private static final Pattern TELEGRAM_PATTERN = Pattern.compile("^@[A-Za-z0-9_]{5,32}$");

    public static void validate(NotificationRequest request) throws RuntimeException {

        if (request.getType() == null) {
            throw new RuntimeException("Тип уведомления обязателен");
        }

        if (request.getRecipient() == null || request.getRecipient().trim().isEmpty()) {
            throw new RuntimeException("Получатель обязателен");
        }

        String recipient = request.getRecipient().trim();

        if (recipient.length() > 255) {
            throw new RuntimeException("Получатель слишком длинный (макс 255 символов)");
        }

        String formatError = checkRecipientFormat(request.getType(), recipient);
        if (formatError != null) {
            log.error(formatError);
        }

        if (request.getSubject() != null && request.getSubject().length() > 100) {
            throw new RuntimeException("Тема слишком длинная (макс 100 символов)");
        }

        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new RuntimeException("Сообщение обязательно");
        } else if (request.getMessage().length() > 2000) {
            throw new RuntimeException("Сообщение слишком длинное (макс 2000 символов)");
        }

        if (request.getPriority() != null) {
            String priority = request.getPriority().toUpperCase();
            if (!priority.equals("HIGH") && !priority.equals("NORMAL") && !priority.equals("LOW")) {
                log.error("Приоритет должен быть HIGH, NORMAL или LOW");
            }
        }

    }

    private static String checkRecipientFormat(NotificationType type, String recipient) {
        switch (type) {
            case EMAIL:
                if (!isValidEmail(recipient)) {
                    return "Для EMAIL получатель должен быть валидным email";
                }
                break;

            case SMS:
                if (!isValidPhone(recipient)) {
                    return "Для SMS получатель должен быть валидным номером телефона";
                }
                break;

            case TELEGRAM:
                if (!isValidTelegram(recipient)) {
                    return "Для TELEGRAM получатель должен быть @username";
                }
                break;

            case ALL:
                break;
        }
        return null;
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidTelegram(String username) {
        if (username == null) return false;
        return TELEGRAM_PATTERN.matcher(username).matches();
    }

    public static boolean isValidPriority(String priority) {
        if (priority == null) return true;
        String upper = priority.toUpperCase();
        return upper.equals("HIGH") || upper.equals("NORMAL") || upper.equals("LOW");
    }

}