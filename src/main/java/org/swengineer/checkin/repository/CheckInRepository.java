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

    // 통계용: 기간 내 유저의 완료된 체크인 habitId 목록 조회
    @Query("""
        SELECT c.habitId FROM CheckIn c
        WHERE c.userId = :userId
          AND c.isCanceled = false
          AND c.checkedDate >= :startDate
          AND c.checkedDate <= :endDate
        """)
    List<Long> findCompletedHabitIdsByPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 통계용: 특정 날짜에 완료된 체크인 habitId 목록
    @Query("""
        SELECT c.habitId FROM CheckIn c
        WHERE c.userId = :userId
          AND c.isCanceled = false
          AND c.checkedDate = :date
        """)
    List<Long> findCompletedHabitIdsByDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    // 히스토리용: 특정 날짜의 완료된 체크인 전체 조회 (CheckIn 엔티티 반환 - 시각 정보 포함)
    @Query("""
        SELECT c FROM CheckIn c
        WHERE c.userId = :userId
          AND c.isCanceled = false
          AND c.checkedDate = :date
        """)
    List<CheckIn> findCompletedCheckInsByDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );
}
