package org.example.promtdeck.global.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum ErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "AUTH_001", "잘못된 요청입니다."),
    //형식 오류
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "AUTH_002", "이메일 형식이 올바르지 않습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "AUTH_003", "비밀번호 형식이 올바르지 않습니다."),
    //회원가입시  이메일 중복 확인
    DUPLICATED_EMAIL(HttpStatus.CONFLICT,"USER_001","이미 사용  중인 이메일입니다." ),
    //User 조회
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_004", "존재하지 않는 사용자입니다."),

    //비밀 번호 틀림
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH_005", "비밀번호가 올바르지 않습니다."),

    //계정 상태
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "AUTH_006", "계정이 잠겨 있습니다."),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "AUTH_007", "비활성화된 계정입니다."),
    ACCOUNT_EXPIRED(HttpStatus.FORBIDDEN, "AUTH_008", "만료된 계정입니다."),


    // JWT 관련
    TOKEN_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH_009", "토큰 생성에 실패했습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN,"AUTH_010","접근 권한이 없습니다."),

    // 기타 인증 관련
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_010", "인증이 필요합니다."),

    // 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_001", "서버 내부 오류가 발생했습니다.");


    private final HttpStatus status;

    private final String code;

    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}
