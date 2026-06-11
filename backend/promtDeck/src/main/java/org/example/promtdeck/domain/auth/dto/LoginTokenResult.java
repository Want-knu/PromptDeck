package org.example.promtdeck.domain.auth.dto;

public record LoginTokenResult(
        LoginResponse response,
        String refreshToken
) {
}
