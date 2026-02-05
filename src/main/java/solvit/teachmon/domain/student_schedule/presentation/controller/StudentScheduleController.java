package solvit.teachmon.domain.student_schedule.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.student_schedule.application.facade.PlaceStudentScheduleService;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.FloorStateResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.PlaceStateResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.PlaceStudentScheduleResponse;
import solvit.teachmon.domain.student_schedule.application.service.StudentScheduleService;
import solvit.teachmon.domain.student_schedule.presentation.dto.request.StudentScheduleCancelRequest;
import solvit.teachmon.domain.student_schedule.presentation.dto.request.StudentScheduleUpdateRequest;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.ClassStudentScheduleResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.HistoryStudentScheduleResponse;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/student-schedule")
@RequiredArgsConstructor
public class StudentScheduleController {
    private final StudentScheduleService studentScheduleService;
    private final PlaceStudentScheduleService placeStudentScheduleService;
    // TODO: 인증 로직 추가 후, 실제 TeacherRepository 주입 무조건 삭제!!
    private final TeacherRepository teacherRepository;

    @GetMapping
    public ResponseEntity<List<ClassStudentScheduleResponse>> getGradeStudentSchedules(
            @RequestParam("grade") @NotNull(message = "학년별 학생 스케줄 조회시 grade(학년)는 필수입니다.") Integer grade,
            @RequestParam("day") @NotNull(message = "학년별 학생 스케줄 조회시 day(날짜)는 필수입니다.") LocalDate day,
            @RequestParam("period") @NotNull(message = "학년별 학생 스케줄 조회시 period(교시)는 필수입니다.") SchoolPeriod period
    ) {
        List<ClassStudentScheduleResponse> results = studentScheduleService.getGradeStudentSchedules(grade, day, period);

        return ResponseEntity
                .ok()
                .body(results);
    }

    @PatchMapping("/{scheduleId}")
    public ResponseEntity<String> updateStudentSchedule(
            @PathVariable("scheduleId") @NotNull(message = "학생 상태 변경에서 scheduleId(스케줄 id)는 필수입니다.") Long scheduleId,
            @RequestBody @Valid StudentScheduleUpdateRequest request
    ) {
        // TODO: 인증 로직 추가 후, 실제 TeacherEntity 주입
        TeacherEntity teacher = teacherRepository.findById(1L).orElseThrow();

        studentScheduleService.updateStudentSchedule(scheduleId, request, teacher);

        return ResponseEntity
                .ok()
                .body("학생 스케줄을 변경 하였습니다.");
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<String> cancelStudentSchedule(
            @PathVariable("scheduleId") @NotNull(message = "학생 상태 변경 취소에서 scheduleId(스케줄 id)는 필수입니다.") Long scheduleId,
            @RequestBody @Valid StudentScheduleCancelRequest request
    ) {
        studentScheduleService.cancelStudentSchedule(scheduleId, request);

        return ResponseEntity
                .ok()
                .body("학생 스케줄 변경을 취소하였습니다.");
    }

    @GetMapping("/history")
    public ResponseEntity<?> getStudentScheduleHistory(
            @RequestParam("day") @NotNull(message = "학생 스케줄 기록 조회에서 day(날짜)는 필수입니다.") LocalDate day,
            @RequestParam("query") String query
    ) {
        List<HistoryStudentScheduleResponse> result = studentScheduleService.getStudentScheduleHistory(query, day);

        return ResponseEntity
                .ok()
                .body(result);
    }

    @GetMapping("/place/state")
    public ResponseEntity<List<FloorStateResponse>> getAllFloorsPlaceCount(
            @RequestParam(value = "day", required = false) LocalDate day,
            @RequestParam(value = "period", required = false) SchoolPeriod period
    ) {
        List<FloorStateResponse> result = placeStudentScheduleService.getAllFloorsPlaceCount(
                getDayOrDefault(day), getPeriodOrDefault(period)
        );

        return ResponseEntity
                .ok()
                .body(result);
    }

    @GetMapping("/place")
    public ResponseEntity<List<PlaceStateResponse>> getPlaceStatesByFloor(
            @RequestParam("floor") @NotNull(message = "층별 장소 상태 조회에서 floor(층)는 필수입니다.") Integer floor,
            @RequestParam(value = "day", required = false) LocalDate day,
            @RequestParam(value = "period", required = false) SchoolPeriod period
    ) {
        List<PlaceStateResponse> result = placeStudentScheduleService.getPlaceStatesByFloor(
                floor, getDayOrDefault(day), getPeriodOrDefault(period)
        );

        return ResponseEntity
                .ok()
                .body(result);
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<PlaceStudentScheduleResponse> getPlaceStudents(
            @PathVariable("placeId") @NotNull(message = "장소별 학생 스케줄 조회에서 placeId(장소 ID)는 필수입니다.") Long placeId,
            @RequestParam(value = "day", required = false) LocalDate day,
            @RequestParam(value = "period", required = false) SchoolPeriod period
    ) {
        PlaceStudentScheduleResponse result = placeStudentScheduleService.getStudentsByPlaceId(
                placeId, getDayOrDefault(day), getPeriodOrDefault(period)
        );

        return ResponseEntity
                .ok()
                .body(result);
    }

    private LocalDate getDayOrDefault(LocalDate day) {
        return (day != null) ? day : LocalDate.now();
    }

    private SchoolPeriod getPeriodOrDefault(SchoolPeriod period) {
        return (period != null) ? period : SchoolPeriod.fromCurrentTime();
    }
}
