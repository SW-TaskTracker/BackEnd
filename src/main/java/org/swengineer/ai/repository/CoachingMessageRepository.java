package org.swengineer.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swengineer.ai.entity.CoachingMessage;

import java.util.Optional;

public interface CoachingMessageRepository extends JpaRepository<CoachingMessage, Long> {
    Optional<CoachingMessage> findTopByUserIdOrderByGeneratedAtDesc(Long userId);
}
