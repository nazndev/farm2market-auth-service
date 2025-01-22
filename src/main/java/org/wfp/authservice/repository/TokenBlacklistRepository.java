package org.wfp.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wfp.authservice.entity.TokenBlacklist;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {

    // Find a token in the blacklist
    Optional<TokenBlacklist> findByToken(String token);

    // Delete expired tokens
    void deleteByExpirationTimeBefore(LocalDateTime now);
}
