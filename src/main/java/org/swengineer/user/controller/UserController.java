package org.swengineer.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.swengineer.auth.util.HeaderUtil;
import org.swengineer.global.api.response.dto.ApiResponse;
import org.swengineer.global.api.response.dto.SuccessResponse;
import org.swengineer.user.code.UserSuccessCode;
import org.swengineer.user.dto.request.UpdateNicknameRequest;
import org.swengineer.user.dto.response.ProfileResponse;
import org.swengineer.user.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 프로필 조회
    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<ProfileResponse>> getProfile(
            @AuthenticationPrincipal Long userId
    ) {
        ProfileResponse profile = userService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.GET_PROFILE_SUCCESS, profile));
    }

    // 닉네임 수정
    @PatchMapping("/me/nickname")
    public ResponseEntity<SuccessResponse<Void>> updateNickname(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateNicknameRequest request
    ) {
        userService.updateNickname(userId, request);
        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.UPDATE_NICKNAME_SUCCESS));
    }

    // 회원탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<SuccessResponse<Void>> withdraw(
            @AuthenticationPrincipal Long userId,
            HttpServletResponse response
    ) {
        userService.withdraw(userId);
        // 탈퇴 시 Authorization 헤더 비워서 클라이언트 토큰 제거 유도
        response.setHeader("Authorization", "");
        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.WITHDRAW_SUCCESS));
    }

    // 로그아웃
    @PostMapping("/me/logout")
    public ResponseEntity<SuccessResponse<Void>> logout(
            @AuthenticationPrincipal Long userId,
            HttpServletResponse response
    ) {
        userService.logout(userId);
        response.setHeader("Authorization", "");
        return ResponseEntity.ok(ApiResponse.success(UserSuccessCode.LOGOUT_SUCCESS));
    }
}
