package org.wfp.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.wfp.authservice.entity.Role;
import org.wfp.authservice.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuditLogService auditLogService;

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }


    public Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> {
                    // Log only critical errors
                    auditLogService.log(
                            "Role",
                            "ERROR",
                            "Role not found with ID: " + id,
                            null,
                            "CRITICAL"
                    );
                    return new RuntimeException("Role not found with ID: " + id);
                });
    }

    public Role saveRole(Role role) {
        boolean isNew = role.getId() == null; // Determine if it's a new role or an update
        Role savedRole = roleRepository.save(role);

        String operation = isNew ? "CREATE" : "UPDATE";
        auditLogService.log(
                "Role",
                operation,
                operation + " role: " + savedRole.getName(),
                null,
                isNew ? "CRITICAL" : "INFO"
        );

        return savedRole;
    }

    public List<Role> findAllRoles() {
        List<Role> roles = roleRepository.findAll();

        // Log large fetches only
        if (roles.size() > 50) {
            auditLogService.log(
                    "Role",
                    "FETCH",
                    "Fetched all roles, count: " + roles.size(),
                    null,
                    "WARN"
            );
        }

        return roles;
    }

    public void deleteRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        // Ensure the role is not assigned to any users before deletion
        if (isRoleAssignedToUsers(roleId)) {
            throw new RuntimeException("Cannot delete role as it is assigned to users");
        }

        roleRepository.deleteById(roleId);

        auditLogService.log(
                "Role",
                "DELETE",
                "Deleted role: " + role.getName(),
                null,
                "CRITICAL"
        );
    }

    public boolean isRoleAssignedToUsers(Long roleId) {
        boolean isAssigned = userService.getAllUsers(PageRequest.of(0, Integer.MAX_VALUE))
                .getContent()
                .stream()
                .anyMatch(user -> user.getRoles().stream().anyMatch(role -> role.getId().equals(roleId)));

        // Log the result only if the role is assigned
        if (isAssigned) {
            auditLogService.log(
                    "Role",
                    "CHECK",
                    "Checked role assignment for ID: " + roleId + ", result: ASSIGNED",
                    null,
                    "INFO"
            );
        }

        return isAssigned;
    }

    public long countRoles() {
        long count = roleRepository.count();

        // Log only if the count exceeds a specific threshold
        if (count > 100) {
            auditLogService.log(
                    "Role",
                    "COUNT",
                    "Counted roles, total: " + count,
                    null,
                    "WARN"
            );
        }

        return count;
    }
}
