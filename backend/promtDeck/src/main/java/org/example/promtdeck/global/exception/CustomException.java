package org.example.promtdeck.global.exception;

import lombok.Getter;
import org.example.promtdeck.global.common.ErrorCode;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
