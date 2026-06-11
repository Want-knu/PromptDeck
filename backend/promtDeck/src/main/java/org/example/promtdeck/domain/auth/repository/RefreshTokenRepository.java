package org.example.promtdeck.domain.auth.repository;

import org.example.promtdeck.domain.auth.entity.RefreshToken;
import org.example.promtdeck.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);

    List<RefreshToken> findAllByUserAndRevokedFalse(User user);

    void deleteByExpiresAtBefore(LocalDateTime now);
}
