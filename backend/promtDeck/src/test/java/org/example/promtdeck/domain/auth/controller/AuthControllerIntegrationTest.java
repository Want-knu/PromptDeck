package org.example.promtdeck.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.example.promtdeck.domain.auth.repository.RefreshTokenRepository;
import org.example.promtdeck.domain.user.entity.User;
import org.example.promtdeck.domain.user.repository.UserRepository;
import org.example.promtdeck.global.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("AuthController 인증 API 통합 테스트")
class AuthControllerIntegrationTest {

    private static final String TRUSTED_ORIGIN = "http://localhost:5173";
    private static final String EMAIL = "user@example.com";
    private static final String PASSWORD = "Password1!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 API 성공 - 사용자를 생성하고 201 응답을 반환한다")
    void signupCreatesUser() throws Exception {
        // given
        Map<String, String> request = Map.of(
                "email", EMAIL,
                "password", PASSWORD,
                "name", "tester"
        );

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(EMAIL))
                .andExpect(jsonPath("$.data.name").value("tester"));

        User savedUser = userRepository.findByEmail(EMAIL).orElseThrow();
        assertThat(passwordEncoder.matches(PASSWORD, savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("회원가입 API 실패 - 요청 값이 유효하지 않으면 400 응답을 반환한다")
    void signupRejectsInvalidRequest() throws Exception {
        // given
        Map<String, String> request = Map.of(
                "email", "invalid-email",
                "password", "short",
                "name", "tester"
        );

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("AUTH_001"));
    }

    @Test
    @DisplayName("회원가입 API 실패 - 중복 이메일이면 409 응답을 반환한다")
    void signupRejectsDuplicatedEmail() throws Exception {
        // given
        saveUser(EMAIL, PASSWORD, "tester");
        Map<String, String> request = Map.of(
                "email", EMAIL,
                "password", PASSWORD,
                "name", "tester"
        );

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)));

        // then
        result.andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("USER_001"));
    }

    @Test
    @DisplayName("로그인 API 성공 - access token과 refresh cookie를 발급한다")
    void loginIssuesAccessTokenAndRefreshCookie() throws Exception {
        // given
        saveUser(EMAIL, PASSWORD, "tester");
        Map<String, String> request = Map.of(
                "email", EMAIL,
                "password", PASSWORD
        );

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)));

        // then
        result.andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.grantType").value("Bearer"))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.accessTokenExpiresIn").value(1_800_000));
        assertThatActiveRefreshTokenCountIs(1);
    }

    @Test
    @DisplayName("로그인 API 실패 - 가입되지 않은 이메일이면 404 응답을 반환한다")
    void loginRejectsUnknownEmail() throws Exception {
        // given
        Map<String, String> request = Map.of(
                "email", EMAIL,
                "password", PASSWORD
        );

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)));

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("AUTH_004"));
    }

    @Test
    @DisplayName("로그인 API 실패 - 비밀번호가 틀리면 401 응답을 반환한다")
    void loginRejectsInvalidPassword() throws Exception {
        // given
        saveUser(EMAIL, PASSWORD, "tester");
        Map<String, String> request = Map.of(
                "email", EMAIL,
                "password", "WrongPassword1!"
        );

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(request)));

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("AUTH_005"));
    }

    @Test
    @DisplayName("인가 실패 - access token 없이 보호 API에 접근하면 401 응답을 반환한다")
    void protectedApiRejectsMissingAccessToken() throws Exception {
        // given

        // when
        ResultActions result = mockMvc.perform(get("/api/provider-keys"));

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("AUTH_014"));
    }

    @Test
    @DisplayName("인가 실패 - 잘못된 access token으로 보호 API에 접근하면 401 응답을 반환한다")
    void protectedApiRejectsInvalidAccessToken() throws Exception {
        // given
        String invalidAccessToken = "invalid-token";

        // when
        ResultActions result = mockMvc.perform(get("/api/provider-keys")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidAccessToken));

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("AUTH_014"));
    }

    @Test
    @DisplayName("인가 성공 - 유효한 access token이면 보호 API 접근을 허용한다")
    void protectedApiAcceptsValidAccessToken() throws Exception {
        // given
        User user = saveUser(EMAIL, PASSWORD, "tester");
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());

        // when
        ResultActions result = mockMvc.perform(get("/api/provider-keys")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("토큰 재발급 API 실패 - 신뢰하지 않는 Origin이면 403 응답을 반환한다")
    void refreshRejectsUntrustedOrigin() throws Exception {
        // given
        saveUser(EMAIL, PASSWORD, "tester");
        Cookie refreshCookie = loginAndExtractRefreshCookie();

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/refresh")
                .header(HttpHeaders.ORIGIN, "http://malicious.example")
                .cookie(refreshCookie));

        // then
        result.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("토큰 재발급 API 성공 - refresh token을 회전하고 새 쿠키를 발급한다")
    void refreshRotatesRefreshToken() throws Exception {
        // given
        saveUser(EMAIL, PASSWORD, "tester");
        Cookie refreshCookie = loginAndExtractRefreshCookie();

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/refresh")
                .header(HttpHeaders.ORIGIN, TRUSTED_ORIGIN)
                .cookie(refreshCookie));

        // then
        result.andExpect(status().isOk())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.grantType").value("Bearer"))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());

        Cookie rotatedCookie = result.andReturn().getResponse().getCookie("refreshToken");
        assertThat(rotatedCookie).isNotNull();
        assertThat(rotatedCookie.getValue()).isNotEqualTo(refreshCookie.getValue());
        assertThatActiveRefreshTokenCountIs(1);
        assertThat(refreshTokenRepository.findAll()).anyMatch(token -> token.isRevoked());
    }

    @Test
    @DisplayName("토큰 재발급 API 실패 - refresh cookie가 없으면 401 응답을 반환한다")
    void refreshRejectsMissingRefreshCookie() throws Exception {
        // given

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/refresh")
                .header(HttpHeaders.ORIGIN, TRUSTED_ORIGIN));

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("AUTH_013"));
    }

    @Test
    @DisplayName("로그아웃 API 성공 - refresh token을 폐기하고 쿠키를 삭제한다")
    void logoutRevokesRefreshTokenAndDeletesCookie() throws Exception {
        // given
        saveUser(EMAIL, PASSWORD, "tester");
        Cookie refreshCookie = loginAndExtractRefreshCookie();

        // when
        ResultActions result = mockMvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.ORIGIN, TRUSTED_ORIGIN)
                .cookie(refreshCookie));

        // then
        result.andExpect(status().isOk())
                .andExpect(cookie().maxAge("refreshToken", 0))
                .andExpect(jsonPath("$.success").value(true));
        assertThat(refreshTokenRepository.findAll()).allMatch(token -> token.isRevoked());
    }

    private User saveUser(String email, String rawPassword, String name) {
        return userRepository.save(User.createLocalUser(
                email,
                passwordEncoder.encode(rawPassword),
                name
        ));
    }

    private Cookie loginAndExtractRefreshCookie() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "email", EMAIL,
                                "password", PASSWORD
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("refreshToken");
        assertThat(cookie).isNotNull();
        return cookie;
    }

    private void assertThatActiveRefreshTokenCountIs(long expectedCount) {
        long activeRefreshTokenCount = refreshTokenRepository.findAll()
                .stream()
                .filter(token -> !token.isRevoked())
                .count();

        assertThat(activeRefreshTokenCount).isEqualTo(expectedCount);
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
