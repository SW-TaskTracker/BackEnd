package org.swengineer.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "아이디는 필수입니다.")
        @Size(min = 4, max = 15, message = "아이디는 4자 이상 15자 이하여야 합니다.")
        @Pattern(regexp = "^[a-z0-9]+$", message = "아이디는 영문 소문자와 숫자만 가능합니다.")
        String routinerId,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글(완성형), 영문, 숫자만 가능합니다.")
        String nickname,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9!@#$%^&*])[a-zA-Z0-9!@#$%^&*]+$",
                message = "비밀번호는 영문, 숫자, 특수문자 중 최소 2가지 조합이어야 합니다.")
        String password
) {}