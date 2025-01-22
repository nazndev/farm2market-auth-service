package org.wfp.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wfp.authservice.dto.ApiResponseDTO;
import org.wfp.authservice.dto.CreateUserDTO;
import org.wfp.authservice.entity.Role;
import org.wfp.authservice.entity.User;
import org.wfp.authservice.service.RoleService;
import org.wfp.authservice.service.UserService;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/protected")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @PreAuthorize("hasAuthority('CREATE_USER')")
    @PostMapping("/users")
    public ResponseEntity<ApiResponseDTO<?>> createUser(@RequestBody CreateUserDTO createUserDTO) {
        if (userService.findByUsername(createUserDTO.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiResponseDTO<>(false, "Username already exists", null));
        }

        if (createUserDTO.getRoles() == null || createUserDTO.getRoles().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponseDTO<>(false, "At least one role must be assigned", null));
        }

        // Transform role IDs to Role entities
        Set<Role> roleEntities = createUserDTO.getRoles().stream()
                .map(roleService::findById)
                .collect(Collectors.toSet());

        // Create a User entity and populate it
        User newUser = new User();
        newUser.setUsername(createUserDTO.getUsername());
        newUser.setEmail(createUserDTO.getEmail());
        newUser.setPassword(userService.encodePassword(createUserDTO.getPassword())); // Encode password
        newUser.setActive(createUserDTO.isActive());
        newUser.setRoles(roleEntities);

        // Save the user
        User savedUser = userService.saveUser(newUser);

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "User created successfully", Map.of(
                "id", savedUser.getId(),
                "username", savedUser.getUsername(),
                "roles", savedUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        )));
    }

    @PreAuthorize("hasAuthority('READ_USER')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponseDTO<?>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userService.getAllUsers(pageable);

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Users fetched successfully", Map.of(
                "users", userPage.getContent(),
                "totalPages", userPage.getTotalPages(),
                "totalElements", userPage.getTotalElements(),
                "currentPage", userPage.getNumber()
        )));
    }

    @PreAuthorize("hasAuthority('READ_USER')")
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponseDTO<?>> getUserById(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "User fetched successfully", user));
    }

    @PreAuthorize("hasAuthority('UPDATE_USER')")
    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponseDTO<?>> updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser
    ) {
        User existingUser = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setActive(updatedUser.isActive());

        userService.saveUser(existingUser);

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "User updated successfully", existingUser));
    }

    @PreAuthorize("hasAuthority('DELETE_USER')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponseDTO<?>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return ResponseEntity.ok(new ApiResponseDTO<>(true, "User deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDTO<>(false, e.getMessage(), null));
        }
    }
}
