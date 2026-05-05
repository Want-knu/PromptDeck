package org.example.promtdeck.domain.auth.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.auth.dto.LoginRequest;
import org.example.promtdeck.domain.auth.dto.LoginResponse;
import org.example.promtdeck.domain.auth.dto.SignupRequest;
import org.example.promtdeck.domain.auth.dto.SignupResponse;
import org.example.promtdeck.domain.user.entity.User;
import org.example.promtdeck.domain.user.repository.UserRepository;
import org.example.promtdeck.global.common.ErrorCode;
import org.example.promtdeck.global.exception.CustomException;
import org.example.promtdeck.global.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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

    @Transactional(readOnly = true)
    public LoginResponse login(@Valid LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());

        return new LoginResponse(
                "Bearer",
                accessToken,
                jwtTokenProvider.getAccessTokenExpiration()
        );
    }

    public void logout() {
        // Stateless JWT logout is handled on the client by removing the token.
    }
}
