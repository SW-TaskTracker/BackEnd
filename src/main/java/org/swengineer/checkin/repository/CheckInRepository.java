package org.swengineer.checkin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.swengineer.checkin.entity.CheckIn;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    // AI-02: 분석 대상 데이터 조회 (취소 제외)
    @Query("""
        SELECT c FROM CheckIn c
        WHERE c.userId = :userId
          AND c.isCanceled = false
          AND c.checkedDate >= :from
        ORDER BY c.checkedAtKst ASC
        """)
    List<CheckIn> findAnalyzableCheckIns(
            @Param("userId") Long userId,
            @Param("from") LocalDate from
    );

    // 오늘 특정 습관 체크인 여부 확인
    @Query("""
        SELECT c FROM CheckIn c
        WHERE c.userId = :userId
          AND c.habitId = :habitId
          AND c.checkedDate = :date
          AND c.isCanceled = false
        """)
    Optional<CheckIn> findTodayCheckIn(
            @Param("userId") Long userId,
            @Param("habitId") Long habitId,
            @Param("date") LocalDate date
    );
}
