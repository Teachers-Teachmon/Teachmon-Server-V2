package solvit.teachmon.domain.management.teacher.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.management.teacher.application.service.ManagementTeacherService;
import solvit.teachmon.domain.management.teacher.presentation.dto.request.TeacherUpdateRequest;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.TeacherListResponse;

import java.util.List;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class ManagementTeacherController {

    private final ManagementTeacherService managementTeacherService;

    @GetMapping
    public ResponseEntity<List<TeacherListResponse>> getTeachers(
            @RequestParam("query") String query
    ) {

        List<TeacherListResponse> teachers = managementTeacherService.getAllTeachers(query);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(teachers);
    }

    @PatchMapping("/{teacher_id}")
    public ResponseEntity<String> updateTeacher(
            @PathVariable("teacher_id") @Min(value = 1, message = "teacher_id는 1이상입니다") Long teacherId,
            @RequestBody @Valid TeacherUpdateRequest updateRequest
    ) {
        managementTeacherService.updateTeacher(updateRequest, teacherId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("선생님 정보를 수정하였습니다");
    }

    @DeleteMapping("/{teacher_id}")
    public ResponseEntity<String> deleteTeacher(
            @PathVariable("teacher_id") @Min(value = 1, message = "teacher_id는 1이상입니다") Long teacherId
    ) {
        managementTeacherService.deleteTeacher(teacherId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("선생님 정보를 삭제하였습니다");
    }
}
