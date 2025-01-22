package org.wfp.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wfp.authservice.dto.ApiResponseDTO;
import org.wfp.authservice.dto.LoginDTO;
import org.wfp.authservice.dto.UserDTO;
import org.wfp.authservice.entity.Role;
import org.wfp.authservice.entity.TokenBlacklist;
import org.wfp.authservice.entity.User;
import org.wfp.authservice.service.*;
import org.wfp.authservice.util.JwtUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/auth/public")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RoleValidationService roleValidationService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<?>> register(@RequestBody UserDTO userDTO) {
        // Validate roles
        if (userDTO.getRoles() == null || userDTO.getRoles().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponseDTO<>(false, "Role is required for registration", null));
        }

        // Validate each role
        for (String role : userDTO.getRoles()) {
            if (!roleValidationService.isAllowedRole(role)) {
                return ResponseEntity.badRequest().body(new ApiResponseDTO<>(false, "Role '" + role + "' is not allowed for registration", null));
            }
        }

        // Create the user
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userService.encodePassword(userDTO.getPassword())); // Encode password
        user.setEmail(userDTO.getEmail());
        user.setActive(true);

        // Assign roles to the user
        Set<Role> roles = userDTO.getRoles().stream()
                .map(roleName -> roleService.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        // Save the user
        userService.saveUser(user);

        // Log registration
        auditLogService.log("User", "REGISTER", "User registered successfully: " + user.getUsername(), null, "INFO");

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "User registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<?>> login(@RequestBody LoginDTO loginDTO) {
        // Find user by username
        System.out.println("Attempting login for username: " + loginDTO.getUsername());
        User user = userService.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        System.out.println("User found: " + user.getUsername());

        if (!userService.checkPassword(loginDTO.getPassword(), user.getPassword())) {
            System.out.println("Password mismatch for username: " + loginDTO.getUsername());
            auditLogService.log("User", "LOGIN_FAILED", "Failed login attempt for: " + loginDTO.getUsername(), null, "WARN");
            return ResponseEntity.badRequest().body(new ApiResponseDTO<>(false, "Invalid username or password", null));
        }
        System.out.println("Password matched for username: " + loginDTO.getUsername());

        // Build JWT claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        claims.put("email", user.getEmail());
        claims.put("active", user.isActive());
        claims.put("permissions", userService.getUserPermissions(user.getId()));

        // Generate token
        String token = jwtUtil.generateToken(user.getUsername(), claims);

        // Extract token expiration time
        Date tokenExpirationDate = jwtUtil.extractExpiration(token);
        long tokenExpiry = tokenExpirationDate.toInstant().getEpochSecond();

        // Log successful login
        auditLogService.log("User", "LOGIN", "User logged in successfully: " + user.getUsername(), null, "INFO");

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("tokenExpiry", tokenExpiry);
        response.put("username", user.getUsername());
        response.put("roles", claims.get("roles"));
        response.put("permissions", claims.get("permissions"));

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDTO<?>> refreshToken(@RequestHeader("Authorization") String token) {
        // Validate Authorization header
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(new ApiResponseDTO<>(false, "Invalid Authorization header", null));
        }

        // Refresh token
        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        String refreshedToken = jwtUtil.refreshToken(jwtToken);
        String username = jwtUtil.extractUsername(jwtToken);

        // Log token refresh
        auditLogService.log("User", "REFRESH_TOKEN", "Token refreshed for user: " + username, null, "INFO");

        return ResponseEntity.ok(new ApiResponseDTO<>(true, "Token refreshed successfully", refreshedToken));
    }

    @GetMapping("/validate-token")
    public ResponseEntity<ApiResponseDTO<?>> validateToken(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(
                    new ApiResponseDTO<>(false, "Invalid Authorization header", null)
            );
        }

        String jwtToken = token.substring(7); // Remove "Bearer " prefix

        if (!jwtUtil.validateToken(jwtToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponseDTO<>(false, "Invalid token", null)
            );
        }

        String username = jwtUtil.extractUsername(jwtToken);
        List<String> roles = jwtUtil.extractRoles(jwtToken);
        List<String> permissions = jwtUtil.extractPermissions(jwtToken);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("username", username);
        responseData.put("roles", roles);
        responseData.put("permissions", permissions);

        return ResponseEntity.ok(
                new ApiResponseDTO<>(true, "Token is valid", responseData)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<?>> logout(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(
                    new ApiResponseDTO<>(false, "Invalid Authorization header", null)
            );
        }

        String jwtToken = token.substring(7); // Remove "Bearer " prefix
        String username;
        Date expirationDate;

        try {
            // Extract username and expiration
            username = jwtUtil.extractUsername(jwtToken);
            expirationDate = jwtUtil.extractExpiration(jwtToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponseDTO<>(false, "Invalid or expired token", null)
            );
        }

        // Convert expiration to LocalDateTime
        LocalDateTime expirationTime = expirationDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // Blacklist the token
        tokenBlacklistService.blacklistToken(jwtToken, expirationTime);

        // Log the logout action
        auditLogService.log("User", "LOGOUT", "User logged out successfully: " + username, null, "INFO");

        // Prepare response data
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("username", username);
        responseData.put("tokenExpiry", expirationTime);

        // Return response
        return ResponseEntity.ok(new ApiResponseDTO<>(
                true,
                "Logout successful",
                responseData
        ));
    }
}
