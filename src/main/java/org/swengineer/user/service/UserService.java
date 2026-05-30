package org.swengineer.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swengineer.global.api.exception.CustomException;
import org.swengineer.habit.repository.HabitRepository;
import org.swengineer.stats.common.AchievementCalculator;
import org.swengineer.user.code.UserErrorCode;
import org.swengineer.user.dto.request.UpdateNicknameRequest;
import org.swengineer.user.dto.response.ProfileResponse;
import org.swengineer.user.entity.User;
import org.swengineer.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final AchievementCalculator achievementCalculator;

    // 프로필 조회
    public ProfileResponse getProfile(Long userId) {
        User user = findActiveUser(userId);
        int activeHabitCount = habitRepository.countByUserIdAndDeletedAtIsNull(userId);
        double monthlyAchievementRate = achievementCalculator.calcThisMonthRate(userId);
        return new ProfileResponse(user.getNickname(), activeHabitCount, monthlyAchievementRate);
    }

    // 닉네임 수정
    @Transactional
    public void updateNickname(Long userId, UpdateNicknameRequest request) {
        User user = findActiveUser(userId);
        user.updateNickname(request.nickname());
    }

    // 회원탈퇴 (soft delete)
    @Transactional
    public void withdraw(Long userId) {
        User user = findActiveUser(userId);
        user.withdraw();
    }

    // 로그아웃 - JWT 무상태 방식이므로 서버에서 할 일 없음
    // 클라이언트가 토큰을 버리는 것으로 처리, 추후 Redis 블랙리스트 도입 시 여기에 로직 추가
    public void logout(Long userId) {
        // no-op
    }

    private User findActiveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        if (user.isWithdrawn()) {
            throw new CustomException(UserErrorCode.ALREADY_WITHDRAWN);
        }
        return user;
    }

    public List<User> getActiveUsers() {
        return userRepository.findAllByDeletedAtIsNull();
    }
}
