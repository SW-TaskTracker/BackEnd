package org.swengineer.global.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.swengineer.global.api.code.ErrorResultCode;
import org.swengineer.global.api.code.GlobalErrorCode;
import org.swengineer.global.api.response.dto.ApiResponse;
import org.swengineer.global.api.response.dto.FailureResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<FailureResponse> handleCustomException(CustomException e) {
        log.warn("CustomException: {}", e.getMessage());
        ErrorResultCode errorCode = e.getErrorCode();
        FailureResponse response = ApiResponse.failure(errorCode);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FailureResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        FailureResponse response = ApiResponse.failure(GlobalErrorCode.INVALID_INPUT_VALUE, message);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FailureResponse> handleException(Exception e) {
        log.error("Unhandled exception: ", e);
        FailureResponse response = ApiResponse.failure(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.internalServerError().body(response);
    }
}