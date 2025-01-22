package org.wfp.authservice.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.wfp.authservice.entity.AuditLog;
import org.wfp.authservice.repository.AuditLogRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // Primary log method with additional metadata support
    public void log(String entityName, String operation, String details, String ipAddress, String logLevel) {
        String performedBy = getCurrentUsername().orElse("SYSTEM"); // Default to SYSTEM if no user is authenticated

        // Validate log level
        String validatedLogLevel = validateLogLevel(logLevel);

        AuditLog log = new AuditLog();
        log.setEntityName(entityName);
        log.setOperation(operation);
        log.setPerformedBy(performedBy);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        log.setIpAddress(ipAddress);
        log.setLogLevel(validatedLogLevel);

        auditLogRepository.save(log);
    }

    // Overloaded log method for backward compatibility
    public void log(String entityName, String operation, String details) {
        log(entityName, operation, details, null, "INFO");
    }

    private Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.of(authentication.getName());
        }
        return Optional.empty();
    }

    // Validate log level (fallback to INFO if invalid)
    private String validateLogLevel(String logLevel) {
        if (logLevel == null) return "INFO";

        switch (logLevel.toUpperCase()) {
            case "INFO":
            case "WARN":
            case "CRITICAL":
                return logLevel.toUpperCase();
            default:
                return "INFO"; // Default to INFO if an invalid level is provided
        }
    }
}
