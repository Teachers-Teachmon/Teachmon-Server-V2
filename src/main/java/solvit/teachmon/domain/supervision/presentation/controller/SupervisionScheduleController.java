package solvit.teachmon.domain.supervision.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solvit.teachmon.domain.supervision.application.service.SupervisionAutoAssignService;
import solvit.teachmon.domain.supervision.application.service.SupervisionScheduleService;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionScheduleCreateRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionScheduleDeleteRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionScheduleUpdateRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionScheduleResponseDto;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionTodayResponseDto;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionRankResponseDto;
import solvit.teachmon.domain.supervision.exception.InvalidDateRangeException;
import solvit.teachmon.domain.supervision.exception.InvalidSupervisionScheduleException;
import solvit.teachmon.global.security.user.TeachmonUserDetails;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/supervision")
@RequiredArgsConstructor
public class SupervisionScheduleController {

    private final SupervisionScheduleService supervisionScheduleService;
    private final SupervisionAutoAssignService supervisionAutoAssignService;
    
    private static final int MAX_ASSIGN_DAYS = 365;

    @PostMapping("/schedule")
    public ResponseEntity<Void> createSupervisionSchedule(@Valid @RequestBody SupervisionScheduleCreateRequestDto requestDto) {
        supervisionScheduleService.createSupervisionSchedule(requestDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/schedule")
    public ResponseEntity<Void> updateSupervisionSchedule(@Valid @RequestBody SupervisionScheduleUpdateRequestDto requestDto) {
        validateUpdateRequest(requestDto);
        supervisionScheduleService.updateSupervisionSchedule(requestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/schedule")
    public ResponseEntity<Void> deleteSupervisionSchedule(@Valid @RequestBody SupervisionScheduleDeleteRequestDto requestDto) {
        supervisionScheduleService.deleteSupervisionSchedule(requestDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SupervisionScheduleResponseDto>> searchSupervisionSchedules(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) String query) {
        validateMonth(month);
        List<SupervisionScheduleResponseDto> responses = supervisionScheduleService.searchSupervisionSchedules(month, query);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/me")
    public ResponseEntity<List<LocalDate>> getMySupervisionDays(
            @RequestParam Integer month,
            @AuthenticationPrincipal TeachmonUserDetails userDetails) {
        validateMonth(month);
        List<LocalDate> supervisionDays = supervisionScheduleService.getMySupervisionDays(userDetails.getId(), month);
        return ResponseEntity.ok(supervisionDays);
    }

    @GetMapping("/today")
    public ResponseEntity<SupervisionTodayResponseDto> getMyTodaySupervisionType(@AuthenticationPrincipal TeachmonUserDetails userDetails) {
        SupervisionTodayResponseDto response = supervisionScheduleService.getMyTodaySupervisionType(userDetails.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rank")
    public ResponseEntity<List<SupervisionRankResponseDto>> getSupervisionRankings(
            @RequestParam(required = false) String query,
            @RequestParam(required = false, defaultValue = "asc") String order) {
        List<SupervisionRankResponseDto> rankings = supervisionScheduleService.getSupervisionRankings(query, order);
        return ResponseEntity.ok(rankings);
    }

    /**
     * 감독 일정 자동 배정 API
     * 우선순위 알고리즘을 적용하여 월~목 평일에 감독 일정을 자동 생성
     */
    @PostMapping("/schedule/auto")
    public ResponseEntity<List<SupervisionScheduleResponseDto>> autoAssignSupervisionSchedules(
            @RequestParam("start_day") LocalDate startDay,
            @RequestParam("end_day") LocalDate endDay) {
        
        validateDateRange(startDay, endDay);
        
        List<SupervisionScheduleResponseDto> responses = 
            supervisionAutoAssignService.autoAssignSupervisionSchedules(startDay, endDay);
        
        return ResponseEntity.ok(responses);
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("시작 날짜가 종료 날짜보다 늦을 수 없습니다.");
        }

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        if (daysBetween > MAX_ASSIGN_DAYS) {
            throw new InvalidDateRangeException("배정 기간은 최대 " + MAX_ASSIGN_DAYS + "일까지 가능합니다.");
        }
    }

    private void validateMonth(Integer month) {
        if (month != null && (month < 1 || month > 12)) {
            throw new InvalidSupervisionScheduleException("월은 1부터 12까지의 값이어야 합니다.");
        }
    }

    private void validateUpdateRequest(SupervisionScheduleUpdateRequestDto requestDto) {
        if (requestDto.selfStudySupervisionTeacherId().equals(requestDto.leaveSeatSupervisionTeacherId())) {
            throw new InvalidSupervisionScheduleException("자습 감독과 이석 감독은 서로 다른 교사여야 합니다.");
        }

        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        
        if (requestDto.day().isBefore(now)) {
            throw new InvalidSupervisionScheduleException("과거 날짜에는 감독 일정을 배정할 수 없습니다.");
        }
    }
}