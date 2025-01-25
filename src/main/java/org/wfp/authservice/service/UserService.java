package org.wfp.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wfp.authservice.entity.Permission;
import org.wfp.authservice.entity.User;
import org.wfp.authservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditLogService auditLogService;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<String> getUserPermissions(Long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .distinct()
                .collect(Collectors.toList());
    }


    public Page<User> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        // Log only critical fetches
        auditLogService.log(
                "User",
                "FETCH",
                "Fetched all users, total: " + users.getTotalElements(),
                null,
                "INFO"
        );

        return users;
    }

    public User saveUser(User user) {
        boolean isNew = user.getId() == null;

        // Encode password
        if (isNew || user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        User savedUser = userRepository.save(user);

        String operation = isNew ? "CREATE" : "UPDATE";
        auditLogService.log(
                "User",
                operation,
                operation + " user: " + savedUser.getUsername(),
                null,
                "INFO"
        );

        return savedUser;
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        boolean isMatched = passwordEncoder.matches(rawPassword, encodedPassword);

        // Avoid logging sensitive details
//        auditLogService.log(
//                "User",
//                "CHECK_PASSWORD",
//                "Password match performed",
//                null,
//                "INFO"
//        );

        return isMatched;
    }

    public Optional<User> findById(Long id) {
        Optional<User> user = userRepository.findById(id);

        // Log only critical fetches
        user.ifPresent(u ->
                auditLogService.log(
                        "User",
                        "FETCH",
                        "Fetched user with ID: " + id,
                        null,
                        "INFO"
                )
        );

        return user;
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        userRepository.deleteById(id);

        auditLogService.log(
                "User",
                "DELETE",
                "Deleted user: " + user.getUsername(),
                null,
                "CRITICAL"
        );
    }
}
