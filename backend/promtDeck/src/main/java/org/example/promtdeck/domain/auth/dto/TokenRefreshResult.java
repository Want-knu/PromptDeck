package org.example.promtdeck.domain.auth.dto;

public record TokenRefreshResult(
        TokenRefreshResponse response,
        String refreshToken
) {
}
