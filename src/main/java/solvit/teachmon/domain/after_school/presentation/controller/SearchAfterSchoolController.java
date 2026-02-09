package solvit.teachmon.domain.after_school.presentation.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solvit.teachmon.domain.after_school.application.service.SearchAfterSchoolService;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolSearchResponseDto;

import java.util.List;

@Validated
@RestController
@RequestMapping("/afterschool")
@RequiredArgsConstructor
public class SearchAfterSchoolController {
    private final SearchAfterSchoolService searchAfterSchoolService;

    @GetMapping("/search")
    public ResponseEntity<List<AfterSchoolSearchResponseDto>> searchAfterSchool(@RequestParam @NotNull(message = "검색어는 필수입니다.") String query) {
        return ResponseEntity.ok(searchAfterSchoolService.searchAfterSchoolByQuery(query));
    }
}