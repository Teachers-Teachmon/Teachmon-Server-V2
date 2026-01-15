package solvit.teachmon.domain.self_study.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.self_study.application.facade.SelfStudyFacadeService;
import solvit.teachmon.domain.self_study.presentation.dto.request.SelfStudySetRequest;

import java.util.List;

@RestController
@RequestMapping("/self-study")
@RequiredArgsConstructor
public class SelfStudyController {
    private final SelfStudyFacadeService selfStudyFacadeService;

    @PostMapping
    public ResponseEntity<String> setSelfStudy(
            @RequestParam("year") Integer year,
            @RequestParam("branch") Integer branch,
            @RequestParam("grade") Integer grade,
            @RequestBody @Valid List<SelfStudySetRequest> request
    ) {
        selfStudyFacadeService.setSelfStudy(year, branch, grade, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("분기별 자습을 설정하였습니다");
    }
}
