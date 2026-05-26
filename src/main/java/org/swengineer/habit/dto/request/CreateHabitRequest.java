package org.swengineer.habit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.swengineer.habit.entity.enums.FrequencyType;
import org.swengineer.habit.entity.enums.HabitCategory;

import java.time.DayOfWeek;
import java.util.Set;

@Schema(description = "습관 등록 요청")
public record CreateHabitRequest(
        @Schema(description = "습관 이름", example = "매일 물 2L 마시기")
        @NotBlank(message = "습관 이름은 필수입니다.")
        @Size(min = 2, max = 20, message = "습관 이름은 2자 이상 20자 이하여야 합니다.")
        String name,

        @Schema(description = "카테고리", example = "HEALTH",
                allowableValues = {"HEALTH", "LEARNING", "PRODUCTIVITY", "ETC"})
        @NotNull(message = "카테고리는 필수입니다.")
        HabitCategory category,

        @Schema(description = "빈도 타입. DAILY=매일, CUSTOM=요일 선택", example = "DAILY")
        @NotNull(message = "빈도 타입은 필수입니다.")
        FrequencyType frequencyType,

        @Schema(description = "선택 요일 목록. frequencyType이 CUSTOM일 때 최소 1개 필수, DAILY일 때는 null 또는 빈 배열",
                example = "[\"MONDAY\", \"WEDNESDAY\", \"FRIDAY\"]",
                allowableValues = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"})
        Set<DayOfWeek> customDays
) {}