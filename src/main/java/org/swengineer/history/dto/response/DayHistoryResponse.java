package org.swengineer.history.dto.response;

import java.time.LocalDate;
import java.util.List;

public record DayHistoryResponse(
        LocalDate date,
        int totalCount,      // 그날 해야 할 습관 수
        int completedCount,  // 실제 완료한 수
        List<HabitHistoryItemResponse> habits
) {
    public static DayHistoryResponse of(LocalDate date, List<HabitHistoryItemResponse> habits) {
        int completedCount = (int) habits.stream().filter(HabitHistoryItemResponse::completed).count();
        return new DayHistoryResponse(date, habits.size(), completedCount, habits);
    }
}
