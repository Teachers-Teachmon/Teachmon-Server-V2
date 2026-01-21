package solvit.teachmon.domain.self_study.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.self_study.application.facade.SelfStudyFacadeService;
import solvit.teachmon.domain.self_study.presentation.dto.common.WeekDaySelfStudyDto;

import java.util.List;

@Validated
@RestController
@RequestMapping("/self-study")
@RequiredArgsConstructor
public class SelfStudyController {
    private final SelfStudyFacadeService selfStudyFacadeService;

    @PostMapping
    public ResponseEntity<String> setSelfStudy(
            @RequestParam("year") @NotNull(message = "자습 설정에서 year(년도)는 필수입니다.") Integer year,
            @RequestParam("branch") @NotNull(message = "자습 설정에서 branch(분기)는 필수입니다.") Integer branch,
            @RequestParam("grade") @NotNull(message = "자습 설정에서 grade(학년)는 필수입니다.") Integer grade,
            @RequestBody @Valid List<WeekDaySelfStudyDto> request
    ) {
        selfStudyFacadeService.setSelfStudy(year, branch, grade, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("분기별 자습을 설정하였습니다");
    }

    @GetMapping
    public ResponseEntity<List<WeekDaySelfStudyDto>> getSelfStudy(
            @RequestParam("year") Integer year,
            @RequestParam("branch") Integer branch,
            @RequestParam("grade") Integer grade
    ) {
        List<WeekDaySelfStudyDto> result = selfStudyFacadeService.getSelfStudy(year, branch, grade);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }
}
