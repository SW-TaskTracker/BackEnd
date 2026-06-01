package org.swengineer.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swengineer.auth.code.AuthSuccessCode;
import org.swengineer.auth.dto.response.LoginResult;
import org.swengineer.auth.util.HeaderUtil;
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
    public ResponseEntity<SuccessResponse<Void>> signUp(
            @Valid @RequestBody SignUpRequest request,
            HttpServletResponse response
    ) {
        TokenResponse token = authService.signUp(request);
        setTokens(response, token);
        return ResponseEntity.ok(ApiResponse.success(AuthSuccessCode.SIGNUP_SUCCESS));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<LoginResult>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResult result = authService.login(request);
        setTokens(response, result.token());

        return ResponseEntity.ok(ApiResponse.success(AuthSuccessCode.LOGIN_SUCCESS, result));
    }

    private void setTokens(HttpServletResponse response, TokenResponse tokens) {
        HeaderUtil.setAuthorizationHeader(response, tokens.accessToken());
    }
}