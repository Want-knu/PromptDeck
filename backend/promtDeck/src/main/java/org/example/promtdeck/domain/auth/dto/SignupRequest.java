package org.example.promtdeck.domain.auth.dto;

import jakarta.validation.constraints.*;

public record SignupRequest(
        @NotBlank(message = "이메일은 필수 입니다,")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자리 이상 20자 이하입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=,*\\d)(?=.*[@$!%*?&]).+$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "이름은 필수입니다.")
        @Max(value = 20,message = "이름은 20자 이하입니다.")
        String name
) {}
