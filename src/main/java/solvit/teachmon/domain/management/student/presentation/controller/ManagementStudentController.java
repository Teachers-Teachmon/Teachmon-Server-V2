package solvit.teachmon.domain.management.student.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.management.student.application.service.ManagementStudentService;
import solvit.teachmon.domain.management.student.presentation.dto.request.StudentRequest;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class ManagementStudentController {

    private final ManagementStudentService managementStudentService;

    @PostMapping
    public ResponseEntity<String> createStudent(
            @RequestBody @Valid StudentRequest request
    ) {
        managementStudentService.createStudent(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("학생이 생성되었습니다");
    }

    @PatchMapping("/{student_id}")
    public ResponseEntity<String> updateStudent(
            @PathVariable("student_id") @Min(value = 1, message = "student_id는 1이상입니다") Long studentId,
            @RequestBody @Valid StudentRequest request
    )  {
        managementStudentService.updateStudent(studentId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("학생 정보가 수정되었습니다");
    }

    @DeleteMapping("/{student_id}")
    public ResponseEntity<String> deleteStudent(
            @PathVariable("student_id") @Min(value = 1, message = "student_id는 1이상입니다") Long studentId
    ) {
        managementStudentService.deleteStudent(studentId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("학생이 삭제되었습니다");
    }
}
