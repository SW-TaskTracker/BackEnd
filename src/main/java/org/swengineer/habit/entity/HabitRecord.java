package org.swengineer.habit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "habit_records",
        uniqueConstraints = @UniqueConstraint(columnNames = {"habit_id", "record_date"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HabitRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(nullable = false)
    private LocalDate recordDate;

    @Column(nullable = false)
    private LocalDateTime checkedInAt;

    public static HabitRecord create(Habit habit, LocalDate date) {
        HabitRecord record = new HabitRecord();
        record.habit = habit;
        record.recordDate = date;
        record.checkedInAt = LocalDateTime.now();
        return record;
    }
}