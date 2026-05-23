package org.example.promtdeck.domain.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import org.example.promtdeck.domain.auth.dto.LoginRequest;
import org.example.promtdeck.domain.auth.dto.LoginTokenResult;
import org.example.promtdeck.domain.auth.dto.SignupRequest;
import org.example.promtdeck.domain.auth.dto.SignupResponse;
import org.example.promtdeck.domain.auth.dto.TokenRefreshResult;
import org.example.promtdeck.domain.auth.entity.RefreshToken;
import org.example.promtdeck.domain.auth.repository.RefreshTokenRepository;
import org.example.promtdeck.domain.user.entity.User;
import org.example.promtdeck.domain.user.repository.UserRepository;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.example.promtdeck.global.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

    private static final String EMAIL = "user@example.com";
    private static final String RAW_PASSWORD = "Password1!";
    private static final String ENCODED_PASSWORD = "encoded-password";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String REFRESH_TOKEN_HASH = "hashed-refresh-token";
    private static final long ACCESS_TOKEN_EXPIRATION = 1_800_000L;
    private static final long REFRESH_TOKEN_EXPIRATION = 1_209_600_000L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RefreshTokenHasher refreshTokenHasher;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtTokenProvider,
                refreshTokenRepository,
                refreshTokenHasher
        );
    }

    @Test
    @DisplayName("회원가입 성공 - 비밀번호를 암호화하고 로컬 사용자를 생성한다")
    void signupCreatesLocalUserWithEncodedPassword() {
        // given
        SignupRequest request = new SignupRequest(EMAIL, RAW_PASSWORD, "tester");

        given(userRepository.existsByEmail(EMAIL)).willReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenAnswer(invocation -> ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        SignupResponse response = authService.signup(request);

        // then
        assertThat(response.email()).isEqualTo(EMAIL);
        assertThat(response.name()).isEqualTo("tester");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue())
                .extracting(User::getEmail, User::getPassword, User::getName)
                .containsExactly(EMAIL, ENCODED_PASSWORD, "tester");
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 사용 중인 이메일이면 예외를 던진다")
    void signupRejectsDuplicatedEmail() {
        // given
        SignupRequest request = new SignupRequest(EMAIL, RAW_PASSWORD, "tester");
        given(userRepository.existsByEmail(EMAIL)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.DUPLICATED_EMAIL);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공 - access token을 발급하고 refresh token hash를 저장한다")
    void loginIssuesAccessTokenAndStoresHashedRefreshToken() {
        // given
        LoginRequest request = new LoginRequest(EMAIL, RAW_PASSWORD);
        User user = User.createLocalUser(EMAIL, ENCODED_PASSWORD, "tester");

        given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).willReturn(true);
        given(jwtTokenProvider.createAccessToken(user.getId())).willReturn(ACCESS_TOKEN);
        given(jwtTokenProvider.createRefreshToken(user.getId())).willReturn(REFRESH_TOKEN);
        given(jwtTokenProvider.getAccessTokenExpiration()).willReturn(ACCESS_TOKEN_EXPIRATION);
        given(jwtTokenProvider.getRefreshTokenExpiration()).willReturn(REFRESH_TOKEN_EXPIRATION);
        when(refreshTokenHasher.hash(anyString())).thenAnswer(invocation -> "hashed-" + invocation.getArgument(0));

        // when
        LoginTokenResult result = authService.login(request);

        // then
        assertThat(result.response().grantType()).isEqualTo("Bearer");
        assertThat(result.response().accessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(result.response().accessTokenExpiresIn()).isEqualTo(ACCESS_TOKEN_EXPIRATION);
        assertThat(result.refreshToken()).isEqualTo(REFRESH_TOKEN);
        verify(refreshTokenRepository).deleteByExpiresAtBefore(any(LocalDateTime.class));

        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
        assertThat(refreshTokenCaptor.getValue().getTokenHash()).isEqualTo(REFRESH_TOKEN_HASH);
        assertThat(refreshTokenCaptor.getValue().isRevoked()).isFalse();
        assertThat(refreshTokenCaptor.getValue().getExpiresAt()).isNotNull();
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호가 일치하지 않으면 예외를 던진다")
    void loginRejectsInvalidPassword() {
        // given
        LoginRequest request = new LoginRequest(EMAIL, "wrong-password");
        User user = User.createLocalUser(EMAIL, ENCODED_PASSWORD, "tester");

        given(userRepository.findByEmail(EMAIL)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong-password", ENCODED_PASSWORD)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PASSWORD);
        verify(jwtTokenProvider, never()).createAccessToken(any());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("토큰 재발급 실패 - refresh token이 없으면 예외를 던진다")
    void refreshRejectsMissingRefreshToken() {
        // given
        String blankRefreshToken = " ";

        // when & then
        assertThatThrownBy(() -> authService.refresh(blankRefreshToken))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
        verify(refreshTokenRepository, never()).findByTokenHashAndRevokedFalse(anyString());
    }

    @Test
    @DisplayName("토큰 재발급 성공 - 기존 refresh token을 폐기하고 새 토큰을 저장한다")
    void refreshRotatesRefreshToken() {
        // given
        User user = User.createLocalUser(EMAIL, ENCODED_PASSWORD, "tester");
        RefreshToken savedRefreshToken = RefreshToken.create(
                user,
                REFRESH_TOKEN_HASH,
                LocalDateTime.now().plusDays(1)
        );

        when(refreshTokenHasher.hash(anyString())).thenAnswer(invocation -> "hashed-" + invocation.getArgument(0));
        given(refreshTokenRepository.findByTokenHashAndRevokedFalse(REFRESH_TOKEN_HASH))
                .willReturn(Optional.of(savedRefreshToken));
        given(jwtTokenProvider.createAccessToken(user.getId())).willReturn("new-access-token");
        given(jwtTokenProvider.createRefreshToken(user.getId())).willReturn("new-refresh-token");
        given(jwtTokenProvider.getAccessTokenExpiration()).willReturn(ACCESS_TOKEN_EXPIRATION);
        given(jwtTokenProvider.getRefreshTokenExpiration()).willReturn(REFRESH_TOKEN_EXPIRATION);

        // when
        TokenRefreshResult result = authService.refresh(REFRESH_TOKEN);

        // then
        assertThat(savedRefreshToken.isRevoked()).isTrue();
        assertThat(result.response().accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");

        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
        assertThat(refreshTokenCaptor.getValue().getTokenHash()).isEqualTo("hashed-new-refresh-token");
        assertThat(refreshTokenCaptor.getValue().isRevoked()).isFalse();
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 만료된 JWT이면 저장된 refresh token을 폐기한다")
    void refreshRevokesSavedTokenWhenJwtIsExpired() {
        // given
        User user = User.createLocalUser(EMAIL, ENCODED_PASSWORD, "tester");
        RefreshToken savedRefreshToken = RefreshToken.create(
                user,
                REFRESH_TOKEN_HASH,
                LocalDateTime.now().plusDays(1)
        );

        when(refreshTokenHasher.hash(anyString())).thenAnswer(invocation -> "hashed-" + invocation.getArgument(0));
        given(refreshTokenRepository.findByTokenHashAndRevokedFalse(REFRESH_TOKEN_HASH))
                .willReturn(Optional.of(savedRefreshToken));
        doThrow(new ExpiredJwtException(null, null, "expired"))
                .when(jwtTokenProvider)
                .validateRefreshToken(REFRESH_TOKEN);

        // when & then
        assertThatThrownBy(() -> authService.refresh(REFRESH_TOKEN))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EXPIRED_TOKEN);
        assertThat(savedRefreshToken.isRevoked()).isTrue();
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("로그아웃 성공 - refresh token cookie가 있으면 저장된 토큰을 폐기한다")
    void logoutRevokesSavedRefreshTokenWhenCookieExists() {
        // given
        User user = User.createLocalUser(EMAIL, ENCODED_PASSWORD, "tester");
        RefreshToken savedRefreshToken = RefreshToken.create(
                user,
                REFRESH_TOKEN_HASH,
                LocalDateTime.now().plusDays(1)
        );

        when(refreshTokenHasher.hash(anyString())).thenAnswer(invocation -> "hashed-" + invocation.getArgument(0));
        given(refreshTokenRepository.findByTokenHashAndRevokedFalse(REFRESH_TOKEN_HASH))
                .willReturn(Optional.of(savedRefreshToken));

        // when
        authService.logout(REFRESH_TOKEN);

        // then
        assertThat(savedRefreshToken.isRevoked()).isTrue();
    }
}
