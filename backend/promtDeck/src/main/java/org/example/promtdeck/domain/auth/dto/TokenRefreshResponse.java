package org.example.promtdeck.domain.auth.dto;

public record TokenRefreshResponse(
        String grantType,
        String accessToken,
        Long accessTokenExpiresIn
) {
}
