package org.wfp.authservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/public")
public class KeyController {

    @Value("${jwt.public-key-path}")
    private String publicKeyPath;

    @GetMapping("/jwks.json")
    public ResponseEntity<Map<String, String>> getPublicKey() {
        try {
            String publicKeyPem = new String(Files.readAllBytes(Paths.get(publicKeyPath)));
            // Remove headers, footers, and line breaks for a JSON-safe format
            String publicKeyContent = publicKeyPem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", ""); // Remove all whitespace

            Map<String, String> response = new HashMap<>();
            response.put("public_key", publicKeyContent);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to load public key"));
        }
    }
}
