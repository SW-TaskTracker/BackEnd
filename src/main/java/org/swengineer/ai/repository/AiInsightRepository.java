package org.swengineer.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swengineer.ai.entity.AiInsight;

import java.util.Optional;

public interface AiInsightRepository extends JpaRepository<AiInsight, Long> {
    //유저의 최신 인사이트 1개
    Optional<AiInsight> findTopByUserIdOrderByGeneratedAtDesc(Long userId);

}
