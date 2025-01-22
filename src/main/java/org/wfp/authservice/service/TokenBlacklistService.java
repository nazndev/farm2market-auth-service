package org.wfp.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wfp.authservice.entity.TokenBlacklist;
import org.wfp.authservice.repository.TokenBlacklistRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TokenBlacklistService {

    @Autowired
    private TokenBlacklistRepository tokenBlacklistRepository;

    public void blacklistToken(String token, LocalDateTime expirationTime) {
        TokenBlacklist blacklistedToken = new TokenBlacklist();
        blacklistedToken.setToken(token);
        blacklistedToken.setExpirationTime(expirationTime);

        tokenBlacklistRepository.save(blacklistedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        Optional<TokenBlacklist> blacklistedToken = tokenBlacklistRepository.findByToken(token);
        return blacklistedToken.isPresent();
    }

    public void cleanExpiredTokens() {
        tokenBlacklistRepository.deleteByExpirationTimeBefore(LocalDateTime.now());
    }
}
