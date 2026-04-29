package org.example.promtdeck.global.common;

public record ErrorResponse(
        boolean sucess,
        String code,
        String message
) {
    public static ErrorResponse of(ErrorCode errorCode){
        return new ErrorResponse(false,errorCode.getCode(),errorCode.getMessage());
    }
}
