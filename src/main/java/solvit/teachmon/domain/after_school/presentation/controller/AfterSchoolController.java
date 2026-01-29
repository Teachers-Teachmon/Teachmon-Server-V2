package solvit.teachmon.domain.after_school.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.after_school.application.service.AfterSchoolService;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolRequestDto;

@RestController
@RequestMapping("/afterschool")
@RequiredArgsConstructor
public class AfterSchoolController {
    private final AfterSchoolService afterSchoolService;

    @PostMapping
    public ResponseEntity<Void> createAfterSchool(@Valid @RequestBody AfterSchoolRequestDto requestDto) {
        afterSchoolService.createAfterSchool(requestDto);
        return ResponseEntity.noContent().build();
    }
}