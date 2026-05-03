package org.example.promtdeck.domain.auth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.promtdeck.domain.auth.dto.LoginRequest;
import org.example.promtdeck.domain.auth.dto.LoginResponse;
import org.example.promtdeck.domain.auth.dto.SignupRequest;
import org.example.promtdeck.domain.auth.dto.SignupResponse;
import org.example.promtdeck.domain.auth.service.AuthService;
import org.example.promtdeck.global.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request){
        SignupResponse response = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.sucess("회원가입에 성공했습니다.",response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.sucess(":로그인에 성공했습니다.",response)
        );
    }

    @PostMapping("logout")
    public ResponseEntity<ApiResponse<Void>> logout(){
        authService.logout();

        return ResponseEntity.ok(
                ApiResponse.sucess("로그아웃에 성공했습니다.")
        );
    }

}
