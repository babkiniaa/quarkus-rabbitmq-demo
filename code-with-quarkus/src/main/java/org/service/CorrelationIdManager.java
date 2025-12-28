package org.service;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static io.quarkus.arc.ComponentsProvider.LOG;

@Slf4j
@ApplicationScoped
public class CorrelationIdManager {

    public String generateCorrelationId() {
        String correlationId = "corr-" + UUID.randomUUID();
        LOG.debugf("Generated correlation ID: %s", correlationId);
        return correlationId;
    }

    public boolean isValidCorrelationId(String correlationId) {
        boolean valid = correlationId != null && correlationId.startsWith("corr-");
        if (!valid) {
            log.warn("Invalid correlation ID: {}", correlationId);
        }
        return valid;
    }
}