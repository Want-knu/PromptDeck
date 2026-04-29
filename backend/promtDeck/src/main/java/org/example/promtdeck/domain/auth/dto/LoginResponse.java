package org.example.promtdeck.domain.auth.dto;

public record LoginResponse(

        String grantType,
        //JWT 토큰
        String accessToken,
        //만료 시간(ms)
        Long accessTokenExpiresIn



) {}
