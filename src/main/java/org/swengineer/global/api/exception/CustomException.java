package org.swengineer.global.api.exception;

import lombok.Getter;
import org.swengineer.global.api.code.ErrorResultCode;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorResultCode errorCode;

    public CustomException(ErrorResultCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}