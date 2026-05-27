package org.swengineer.habit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "habit_records",
        uniqueConstraints = @UniqueConstraint(columnNames = {"habit_id", "record_date"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HabitRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long habitId;

    @Column(nullable = false)
    private LocalDate recordDate;

    public static HabitRecord create(Long habitId, LocalDate date) {
        HabitRecord record = new HabitRecord();
        record.habitId = habitId;
        record.recordDate = date;
        return record;
    }
}