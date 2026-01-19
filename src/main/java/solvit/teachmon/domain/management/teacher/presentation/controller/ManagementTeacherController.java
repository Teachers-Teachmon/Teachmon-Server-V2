package solvit.teachmon.domain.management.teacher.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.management.teacher.application.facade.ManagementTeacherFacadeService;
import solvit.teachmon.domain.management.teacher.application.service.ManagementTeacherService;
import solvit.teachmon.domain.management.teacher.presentation.dto.request.TeacherUpdateRequest;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.TeacherListResponse;
import solvit.teachmon.global.enums.WeekDay;

import java.util.List;

@Validated
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class ManagementTeacherController {

    private final ManagementTeacherService managementTeacherService;
    private final ManagementTeacherFacadeService managementTeacherFacadeService;

    @GetMapping
    public ResponseEntity<List<TeacherListResponse>> getTeachers(
            @RequestParam("query") String query
    ) {

        List<TeacherListResponse> teachers = managementTeacherFacadeService.getAllTeachers(query);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teachers);
    }

    @PatchMapping("/{teacher_id}")
    public ResponseEntity<String> updateTeacher(
            @PathVariable("teacher_id") @Min(value = 1, message = "teacher_id는 1이상입니다") Long teacherId,
            @RequestBody @Valid TeacherUpdateRequest updateRequest
    ) {
        managementTeacherFacadeService.updateTeacher(updateRequest, teacherId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("선생님 정보를 수정하였습니다");
    }

    @DeleteMapping("/{teacher_id}")
    public ResponseEntity<String> deleteTeacher(
            @PathVariable("teacher_id") @Min(value = 1, message = "teacher_id는 1이상입니다") Long teacherId
    ) {
        managementTeacherFacadeService.deleteTeacher(teacherId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("선생님 정보를 삭제하였습니다");
    }

    @GetMapping("/{teacher_id}/ban")
    public ResponseEntity<List<WeekDay>> getTeacherBanDay(
            @PathVariable("teacher_id") @Min(value = 1, message = "teacher_id는 1이상입니다") Long teacherId
    ) {
        List<WeekDay> banDays = managementTeacherService.getTeacherBanDay(teacherId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(banDays);
    }

    @PostMapping("/{teacher_id}/ban")
    public ResponseEntity<String> setTeacherBanDay(
            @PathVariable("teacher_id") @Min(value = 1, message = "teacher_id는 1이상입니다") Long teacherId,
            @RequestBody @Valid List<WeekDay> banDays
    ) {
        managementTeacherFacadeService.setTeacherBanDay(teacherId, banDays);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("선생님 금지날이 설정되었습니다");
    }
}
