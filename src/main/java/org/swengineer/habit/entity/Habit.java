package org.swengineer.habit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swengineer.common.base.BaseEntity;
import org.swengineer.habit.entity.enums.FrequencyType;
import org.swengineer.habit.entity.enums.HabitCategory;

import java.time.DayOfWeek;
import java.util.Set;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FrequencyType frequencyType;

    @ElementCollection
    @CollectionTable(name = "habit_days", joinColumns = @JoinColumn(name = "habit_id"))
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    private Set<java.time.DayOfWeek> customDays;

    public static Habit create(Long userId, String name,
                               HabitCategory category,
                               FrequencyType frequencyType,
                               Set<java.time.DayOfWeek> customDays) {
        Habit habit = new Habit();
        habit.userId = userId;
        habit.name = name;
        habit.category = category;
        habit.frequencyType = frequencyType;
        habit.customDays = frequencyType == FrequencyType.DAILY
                ? Set.of(java.time.DayOfWeek.values())
                : customDays;
        return habit;
    }

    public void update(String name, HabitCategory category,
                       FrequencyType frequencyType,
                       Set<DayOfWeek> customDays) {
        this.name = name;
        this.category = category;
        this.frequencyType = frequencyType;
        this.customDays = frequencyType == FrequencyType.DAILY
                ? Set.of(java.time.DayOfWeek.values())
                : customDays;
    }
}
