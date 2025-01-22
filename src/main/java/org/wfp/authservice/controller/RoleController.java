package org.wfp.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wfp.authservice.dto.ApiResponseDTO;
import org.wfp.authservice.entity.Role;
import org.wfp.authservice.entity.User;
import org.wfp.authservice.service.RoleService;
import org.wfp.authservice.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/protected/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('MANAGE_ROLE')")
    @PostMapping
    public ResponseEntity<ApiResponseDTO<?>> createRole(@RequestBody Role role) {
        if (roleService.findByName(role.getName()).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponseDTO<>(false, "Role already exists", null));
        }

        Role savedRole = roleService.saveRole(role);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Role created successfully", savedRole));
    }

    @PreAuthorize("hasAuthority('MANAGE_ROLE')")
    @PutMapping("/{roleId}")
    public ResponseEntity<ApiResponseDTO<?>> updateRole(
            @PathVariable Long roleId,
            @RequestBody Role updatedRole) {
        Role role = roleService.findById(roleId);
        if (role == null) {
            throw new RuntimeException("Role not found with ID: " + roleId);
        }

        role.setName(updatedRole.getName());
        roleService.saveRole(role);

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Role updated successfully", role));
    }


    @PreAuthorize("hasAuthority('ASSIGN_ROLE')")
    @PostMapping("/{username}/assign")
    public ResponseEntity<ApiResponseDTO<?>> assignRolesToUser(
            @PathVariable String username,
            @RequestBody List<String> roles) {

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> roleSet = roles.stream()
                .map(roleName -> roleService.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roleSet);
        userService.saveUser(user);

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Roles assigned successfully to user: " + username, null));
    }

    @PreAuthorize("hasAuthority('MANAGE_ROLE')")
    @DeleteMapping("/{roleId}")
    public ResponseEntity<ApiResponseDTO<?>> deleteRole(@PathVariable Long roleId) {
        if (roleService.isRoleAssignedToUsers(roleId)) {
            return ResponseEntity.badRequest().body(new ApiResponseDTO<>(false, "Role is assigned to users and cannot be deleted", null));
        }

        roleService.deleteRoleById(roleId);
        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Role deleted successfully", null));
    }

    @PreAuthorize("hasAuthority('ACCESS_DASHBOARD')")
    @GetMapping
    public ResponseEntity<ApiResponseDTO<?>> getAllRoles() {
        List<Role> roles = roleService.findAllRoles();
        long totalRoles = roleService.countRoles();

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Roles fetched successfully", Map.of(
                "roles", roles,
                "totalRoles", totalRoles
        )));
    }
}
