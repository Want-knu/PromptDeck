package org.example.promtdeck.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    private static final String TOKEN_TYPE_CLAIM = "tokenType";
    private static final String ACCESS = "ACCESS";
    private static final String REFRESH = "REFRESH";


    private final SecretKey secretKey;
    @Getter
    private final long accessTokenExpiration;
    @Getter
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * 로그인 성공시 사용자의 고유ID를 담은 토큰 생성하는 메서드
     */
    public String createAccessToken(Long userId) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .claim(TOKEN_TYPE_CLAIM, ACCESS)
                .subject(String.valueOf(userId))
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(TOKEN_TYPE_CLAIM,REFRESH)
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(secretKey)
                .compact();
    }

    /**
     *
     * 클라이언트가 보낸 토큰이 유효한지 검사하는 '검독기' 역할을 합니다.
     */
    public void validateAccessToken(String token) {
        Claims claims = parseClaims(token);
        if(!ACCESS.equals(claims.get(TOKEN_TYPE_CLAIM,String.class))) {
            throw new JwtException("Invalid access token");
        }
    }
    public void validateRefreshToken(String token) {
        Claims claims = parseClaims(token);
        if(!REFRESH.equals(claims.get(TOKEN_TYPE_CLAIM,String.class))) {
            throw new JwtException("Invalid refresh token");
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    /**
     *
     * 검증된 토큰에서 "이 토큰은 누구의 것인가?"를 알아내는 메서드입니다.
     */
    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }
}
