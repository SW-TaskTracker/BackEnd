package org.swengineer.checkin.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.swengineer.common.base.BaseEntity;

import java.time.*;

@Entity
@Table(name = "check_ins",
        indexes = {
                // AI-02 배치 쿼리 성능용 인덱스
                @Index(name = "idx_checkin_user_canceled",
                        columnList = "userId, isCanceled")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckIn extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long habitId;

    // AI-01: KST 기준 시각
    @Column(nullable = false)
    private LocalDateTime checkedAtKst;

    // AI-01: 요일
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    // AI-01: 날짜
    @Column(nullable = false)
    private LocalDate checkedDate;

    // AI-01: 취소 여부
    @Column(nullable = false)
    private boolean isCanceled = false;

    @Column
    private LocalDateTime canceledAt;

    public static CheckIn create(Long userId, Long habitId) {
        CheckIn checkIn = new CheckIn();
        checkIn.userId = userId;
        checkIn.habitId = habitId;

        // AI-01: UTC → KST 변환 후 저장
        ZonedDateTime kst = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        checkIn.checkedAtKst = kst.toLocalDateTime();
        checkIn.dayOfWeek = kst.getDayOfWeek();
        checkIn.checkedDate = kst.toLocalDate();

        return checkIn;
    }

    // AI-01: 취소 시 즉시 분석 제외 처리
    public void cancel() {
        this.isCanceled = true;
        this.canceledAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
