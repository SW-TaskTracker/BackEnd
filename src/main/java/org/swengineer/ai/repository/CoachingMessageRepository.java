package org.swengineer.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swengineer.ai.entity.CoachingMessage;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CoachingMessageRepository extends JpaRepository<CoachingMessage, Long> {
    Optional<CoachingMessage> findTopByUserIdOrderByGeneratedAtDesc(Long userId);

    // 오늘 07:00 ~ 내일 07:00 범위의 메시지 조회
    Optional<CoachingMessage> findTopByUserIdAndGeneratedAtBetweenOrderByGeneratedAtDesc(
            Long userId, LocalDateTime start, LocalDateTime end
    );
}
