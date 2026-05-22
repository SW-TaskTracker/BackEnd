package org.swengineer.global.api.code;

import org.springframework.http.HttpStatus;

public interface ResultCode {
    HttpStatus getStatus();
    String getMessage();
    String name();
}