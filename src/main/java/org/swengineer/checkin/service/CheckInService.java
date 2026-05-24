package org.swengineer.checkin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swengineer.checkin.code.CheckInErrorCode;
import org.swengineer.checkin.dto.response.CheckInResponse;
import org.swengineer.checkin.entity.CheckIn;
import org.swengineer.checkin.repository.CheckInRepository;
import org.swengineer.global.api.exception.CustomException;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final CheckInRepository checkInRepository;

    // 체크인
    @Transactional
    public CheckInResponse checkIn(Long userId, Long habitId) {

        // 오늘 이미 체크인했는지 확인 (KST 기준)
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        checkInRepository.findTodayCheckIn(userId, habitId, today)
                .ifPresent(c -> {
                    throw new CustomException(CheckInErrorCode.CHECK_IN_ALREADY_EXISTS);
                });

        CheckIn checkIn = CheckIn.create(userId, habitId);
        return CheckInResponse.from(checkInRepository.save(checkIn));
    }

    //체크인 취소
    @Transactional
    public void cancel(Long userId, Long checkInId) {

        CheckIn checkIn = checkInRepository.findById(checkInId)
                .orElseThrow(() -> new CustomException(CheckInErrorCode.CHECK_IN_NOT_FOUND));

        if (!checkIn.getUserId().equals(userId)) {
            throw new CustomException(CheckInErrorCode.CHECK_IN_FORBIDDEN);
        }

        if (checkIn.isCanceled()) {
            throw new CustomException(CheckInErrorCode.CHECK_IN_ALREADY_CANCELED);
        }

        checkIn.cancel();
    }
}
