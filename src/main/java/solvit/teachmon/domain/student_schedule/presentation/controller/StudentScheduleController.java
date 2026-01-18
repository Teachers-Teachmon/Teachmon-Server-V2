package solvit.teachmon.domain.student_schedule.presentation.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solvit.teachmon.domain.student_schedule.application.facade.StudentScheduleFacadeService;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.ClassStudentScheduleResponse;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/student-schedule")
@RequiredArgsConstructor
public class StudentScheduleController {
    private final StudentScheduleFacadeService studentScheduleFacadeService;

    @GetMapping
    public ResponseEntity<List<ClassStudentScheduleResponse>> getGradeStudentSchedules(
            @RequestParam("grade") @NotNull(message = "학년별 학생 스케줄 조회시 grade(학년)는 필수입니다") Integer grade,
            @RequestParam("day") @NotNull(message = "학년별 학생 스케줄 조회시 day(날짜)는 필수입니다") LocalDate day,
            @RequestParam("period") @NotNull(message = "학년별 학생 스케줄 조회시 period(교시)는 필수입니다") SchoolPeriod period
    ) {
        List<ClassStudentScheduleResponse> results = studentScheduleFacadeService.getGradeStudentSchedules(grade, day, period);

        return ResponseEntity
                .ok()
                .body(results);
    }
}
