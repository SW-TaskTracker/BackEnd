package org.swengineer.history.dto.response;

import org.swengineer.habit.entity.Habit;
import org.swengineer.habit.entity.enums.HabitCategory;

public record HabitHistoryItemResponse(
        Long habitId,
        String name,
        HabitCategory category,
        boolean completed,
        String checkedAtKst  // 완료한 경우 체크인 시각 (HH:mm), 미완료면 null
) {
    public static HabitHistoryItemResponse of(Habit habit, boolean completed, String checkedAtKst) {
        return new HabitHistoryItemResponse(
                habit.getId(),
                habit.getName(),
                habit.getCategory(),
                completed,
                checkedAtKst
        );
    }
}
