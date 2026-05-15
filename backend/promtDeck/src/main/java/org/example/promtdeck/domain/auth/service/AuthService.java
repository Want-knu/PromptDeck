package org.example.promtdeck.domain.auth.service;

import jakarta.validation.Valid;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.auth.dto.*;
import org.example.promtdeck.domain.auth.entity.RefreshToken;
import org.example.promtdeck.domain.auth.repository.RefreshTokenRepository;
import org.example.promtdeck.domain.user.entity.User;
import org.example.promtdeck.domain.user.repository.UserRepository;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.example.promtdeck.global.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenHasher refreshTokenHasher;

    @Transactional
    public SignupResponse signup(@Valid SignupRequest request) {
        if(userRepository.existsByEmail(request.email())){
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }
        String encodedPassword = passwordEncoder.encode(request.password());

        User user = User.createLocalUser(
                request.email(),
                encodedPassword,
                request.name()
        );

        User savedUser = userRepository.save(user);

        return new SignupResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName()
        );
    }

    @Transactional
    public LoginTokenResult login(@Valid LoginRequest request) {
        cleanupExpiredRefreshTokens();
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        saveRefreshToken(user, refreshToken);
        return new LoginTokenResult(
                new LoginResponse("Bearer", accessToken, jwtTokenProvider.getAccessTokenExpiration()),
                refreshToken
        );
    }

    @Transactional
    public TokenRefreshResult refresh(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String tokenHash = refreshTokenHasher.hash(refreshToken);
        try {
            jwtTokenProvider.validateRefreshToken(refreshToken);
        } catch (ExpiredJwtException e) {
            refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
                    .ifPresent(RefreshToken::revoke);
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken savedToken = refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (savedToken.isExpired()) {
            savedToken.revoke();
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        User user = savedToken.getUser();

        savedToken.revoke();

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        cleanupExpiredRefreshTokens();
        saveRefreshToken(user, newRefreshToken);

        return new TokenRefreshResult(
                new TokenRefreshResponse("Bearer", newAccessToken, jwtTokenProvider.getAccessTokenExpiration()),
                newRefreshToken
        );
    }
    @Transactional
    public void logout(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return;
        }

        String tokenHash = refreshTokenHasher.hash(refreshToken);
        refreshTokenRepository.findByTokenHashAndRevokedFalse(tokenHash)
                .ifPresent(RefreshToken::revoke);
    }

    private void saveRefreshToken(User user, String refreshToken) {
        refreshTokenRepository.save(RefreshToken.create(
                user,
                refreshTokenHasher.hash(refreshToken),
                LocalDateTime.now().plus(Duration.ofMillis(jwtTokenProvider.getRefreshTokenExpiration()))
        ));
    }

    private void cleanupExpiredRefreshTokens() {
        refreshTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
