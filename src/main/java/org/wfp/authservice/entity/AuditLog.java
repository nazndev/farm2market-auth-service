package org.wfp.authservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityName; // Name of the entity involved in the operation
    private String operation;  // Type of operation (CREATE, UPDATE, DELETE, etc.)
    private String performedBy; // Username of the user who performed the operation

    @Column(columnDefinition = "TEXT")
    private String details; // Detailed description of the operation

    private LocalDateTime timestamp; // Timestamp of the operation

    private String ipAddress; // IP address of the request (optional)
    private String logLevel;  // Log level (e.g., INFO, WARN, CRITICAL)
}
