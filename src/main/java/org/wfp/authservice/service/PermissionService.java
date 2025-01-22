package org.wfp.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wfp.authservice.entity.Permission;
import org.wfp.authservice.repository.PermissionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private AuditLogService auditLogService;

    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }

    public Optional<Permission> findByName(String name) {
        return permissionRepository.findByName(name);
    }

    public Permission savePermission(Permission permission) {
        boolean isNew = permission.getId() == null; // Determine if it's a new permission or an update
        Permission savedPermission = permissionRepository.save(permission);

        String operation = isNew ? "CREATE" : "UPDATE";
        auditLogService.log(
                "Permission",
                operation,
                operation + " permission: " + savedPermission.getName(),
                null, // No IP context here
                isNew ? "CRITICAL" : "INFO" // Critical for creation, informational for updates
        );

        return savedPermission;
    }

    public List<Permission> findAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();

        // Log only if the fetched count exceeds a threshold (e.g., 100)
        if (permissions.size() > 100) {
            auditLogService.log(
                    "Permission",
                    "FETCH",
                    "Fetched a large number of permissions, count: " + permissions.size(),
                    null,
                    "WARN"
            );
        }

        return permissions;
    }

    public void deletePermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with ID: " + id));

        permissionRepository.deleteById(id);

        auditLogService.log(
                "Permission",
                "DELETE",
                "Deleted permission: " + permission.getName(),
                null,
                "CRITICAL" // Deletion is critical
        );
    }

    public long countPermissions() {
        long count = permissionRepository.count();

        // Log only if count exceeds a specific threshold (e.g., 1000)
        if (count > 1000) {
            auditLogService.log(
                    "Permission",
                    "COUNT",
                    "Counted permissions, total: " + count,
                    null,
                    "WARN"
            );
        }

        return count;
    }
}
