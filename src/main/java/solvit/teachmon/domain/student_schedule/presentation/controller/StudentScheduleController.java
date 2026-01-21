package solvit.teachmon.domain.student_schedule.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.student_schedule.application.service.StudentScheduleService;
import solvit.teachmon.domain.student_schedule.presentation.dto.request.StudentScheduleCancelRequest;
import solvit.teachmon.domain.student_schedule.presentation.dto.request.StudentScheduleUpdateRequest;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.ClassStudentScheduleResponse;
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
}
