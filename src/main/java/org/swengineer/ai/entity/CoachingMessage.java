package org.swengineer.ai.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "coaching_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoachingMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    public static CoachingMessage create(Long userId, String content) {
        CoachingMessage message = new CoachingMessage();
        message.userId = userId;
        message.content = content;
        message.generatedAt = LocalDateTime.now();
        return message;
    }
}
