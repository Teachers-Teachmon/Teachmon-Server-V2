package solvit.teachmon.domain.management.teacher.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import solvit.teachmon.domain.management.teacher.application.service.ManagementTeacherService;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.TeacherListResponse;

import java.util.List;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class ManagementTeacherController {

    private final ManagementTeacherService managementTeacherService;

    @GetMapping("/teachers")
    public ResponseEntity<List<TeacherListResponse>> getTeachers() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(managementTeacherService.getAllTeachers());
    }
}
