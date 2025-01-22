package org.wfp.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wfp.authservice.dto.ApiResponseDTO;
import org.wfp.authservice.entity.Permission;
import org.wfp.authservice.service.PermissionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/protected/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    @PostMapping
    public ResponseEntity<ApiResponseDTO<?>> createPermission(@RequestBody Permission permission) {
        if (permissionService.findByName(permission.getName()).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponseDTO<>(false, "Permission already exists", null));
        }

        Permission savedPermission = permissionService.savePermission(permission);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Permission created successfully", savedPermission));
    }


    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    @PutMapping("/{permissionId}")
    public ResponseEntity<ApiResponseDTO<?>> updatePermission(
            @PathVariable Long permissionId,
            @RequestBody Permission updatedPermission) {
        Permission permission = permissionService.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with ID: " + permissionId));

        permission.setName(updatedPermission.getName());
        permissionService.savePermission(permission);

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Permission updated successfully", permission));
    }

    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    @DeleteMapping("/{permissionId}")
    public ResponseEntity<ApiResponseDTO<?>> deletePermission(@PathVariable Long permissionId) {
        permissionService.deletePermissionById(permissionId);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Permission deleted successfully", null));
    }

    @PreAuthorize("hasAuthority('ACCESS_DASHBOARD')")
    @GetMapping
    public ResponseEntity<ApiResponseDTO<?>> getAllPermissions() {
        List<Permission> permissions = permissionService.findAllPermissions();
        long totalPermissions = permissionService.countPermissions();

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Permissions fetched successfully", Map.of(
                "permissions", permissions,
                "totalPermissions", totalPermissions
        )));
    }
}
