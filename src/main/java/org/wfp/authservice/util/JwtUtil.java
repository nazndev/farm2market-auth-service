package org.wfp.authservice.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.private-key-path}")
    private String privateKeyPath;

    @Value("${jwt.public-key-path}")
    private String publicKeyPath;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void initializeKeys() throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Load private key from file
        String privateKeyPem = new String(Files.readAllBytes(Path.of(privateKeyPath)));
        String privateKeyContent = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", ""); // Remove all whitespace
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        this.privateKey = keyFactory.generatePrivate(privateKeySpec);

        // Load public key from file
        String publicKeyPem = new String(Files.readAllBytes(Path.of(publicKeyPath)));
        String publicKeyContent = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", ""); // Remove all whitespace
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        this.publicKey = keyFactory.generatePublic(publicKeySpec);
    }

    public String generateToken(String username, Map<String, Object> claims) {
        claims.put("created", new Date());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    public List<String> extractPermissions(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("permissions", List.class);
    }

    public String refreshToken(String token) {
        Claims claims = extractAllClaims(token);
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(System.currentTimeMillis() + jwtExpiration));
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }
}
