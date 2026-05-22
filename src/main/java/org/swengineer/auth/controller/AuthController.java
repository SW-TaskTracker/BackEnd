package org.swengineer.auth.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swengineer.auth.code.AuthSuccessCode;
import org.swengineer.global.api.response.dto.ApiResponse;
import org.swengineer.auth.dto.request.LoginRequest;
import org.swengineer.auth.dto.request.SignUpRequest;
import org.swengineer.auth.dto.response.TokenResponse;
import org.swengineer.auth.service.AuthService;
import org.swengineer.global.api.response.dto.SuccessResponse;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<Void>> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.ok(ApiResponse.success(AuthSuccessCode.SIGNUP_SUCCESS));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(AuthSuccessCode.LOGIN_SUCCESS,token));
    }
}