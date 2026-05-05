package org.example.promtdeck.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    @Getter
    private final long accessTokenExpiration;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.access-token-expiration}") long accessTokenExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
    }

    /**
     * 로그인 성공시 사용자의 고유ID를 담은 토큰 생성하는 메서드
     */
    public String createAccessToken(Long userId) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }

    /**
     *
     * 클라이언트가 보낸 토큰이 유효한지 검사하는 '검독기' 역할을 합니다.
     */
    public boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
        return true;
    }

    /**
     *
     * 검증된 토큰에서 "이 토큰은 누구의 것인가?"를 알아내는 메서드입니다.
     */
    public Long getUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.parseLong(claims.getSubject());
    }
}
