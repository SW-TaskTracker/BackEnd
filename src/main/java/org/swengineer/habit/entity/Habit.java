package org.swengineer.habit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swengineer.common.base.BaseEntity;
import org.swengineer.habit.entity.enums.HabitCategory;

import java.time.LocalDateTime;

@Entity
@Table(name = "habits")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Habit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    //습관 이름
    @Column(nullable = false)
    private String name;

    //습관 카테고리

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HabitCategory category;

    //반복단위
    @Column(nullable = false)
    private int targetCountPerWeek;//1-7 선택



    //습관 반복
    public static Habit create(Long userId, String name,
                               HabitCategory category,
                               int targetCountPerWeek) {
        // targetCountPerWeek 유효성 검사
        if (targetCountPerWeek < 1 || targetCountPerWeek > 7) {
            throw new IllegalArgumentException("주 목표 횟수는 1~7 사이여야 합니다.");
        }
        Habit habit = new Habit();
        habit.userId = userId;
        habit.name = name;
        habit.category = category;
        habit.targetCountPerWeek = targetCountPerWeek;
        return habit;
    }
}
