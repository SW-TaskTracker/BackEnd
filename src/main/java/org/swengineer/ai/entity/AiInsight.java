package org.swengineer.ai.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_insights")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // AI가 생성한 인사이트 텍스트

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    public static AiInsight create(Long userId, String content) {
        AiInsight insight = new AiInsight();
        insight.userId = userId;
        insight.content = content;
        insight.generatedAt = LocalDateTime.now();
        return insight;
    }

}
