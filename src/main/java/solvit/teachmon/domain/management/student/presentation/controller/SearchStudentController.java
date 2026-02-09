package solvit.teachmon.domain.management.student.presentation.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solvit.teachmon.domain.management.student.application.service.SearchStudentService;
import solvit.teachmon.domain.management.student.presentation.dto.response.StudentSearchResponseDto;

import java.util.List;

@Validated
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class SearchStudentController {
    private final SearchStudentService searchStudentService;

    @GetMapping("/search")
    public ResponseEntity<List<StudentSearchResponseDto>> searchStudent(@RequestParam @NotNull(message = "검색어는 필수입니다.") String query) {
        return ResponseEntity.ok(searchStudentService.searchStudentByQuery(query));
    }
}
