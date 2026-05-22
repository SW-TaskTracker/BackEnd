package org.swengineer.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swengineer.auth.code.AuthErrorCode;
import org.swengineer.auth.dto.request.LoginRequest;
import org.swengineer.auth.dto.request.SignUpRequest;
import org.swengineer.auth.dto.response.TokenResponse;
import org.swengineer.global.api.config.JwtTokenProvider;
import org.swengineer.global.api.exception.CustomException;
import org.swengineer.user.entity.User;
import org.swengineer.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse signUp(SignUpRequest request) {
        if (userRepository.existsByRoutinerId(request.routinerId())) {
            throw new CustomException(AuthErrorCode.DUPLICATE_ROUTINER_ID);
        }

        User user = User.create(
                request.routinerId(),
                request.nickname(),
                passwordEncoder.encode(request.password())
        );
        userRepository.save(user);

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRoutinerId());
        return new TokenResponse(accessToken);
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByRoutinerId(request.routinerId())
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(AuthErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRoutinerId());
        return new TokenResponse(accessToken);
    }
}