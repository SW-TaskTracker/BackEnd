package org.swengineer.checkin.dto.request;

import jakarta.validation.constraints.NotNull;

    public record CheckInRequest(
            @NotNull(message = "habitId는 필수입니다.")
            Long habitId
    ) {}

