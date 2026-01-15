package solvit.teachmon.domain.self_study.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.self_study.application.service.AdditionalSelfStudyService;
import solvit.teachmon.domain.self_study.presentation.dto.request.AdditionalSelfStudySetRequest;

@RestController
@RequestMapping("/self-study/additional")
@RequiredArgsConstructor
public class AdditionalSelfStudyController {

    private final AdditionalSelfStudyService additionalSelfStudyService;

    @PostMapping
    public ResponseEntity<String> setAdditionalSelfStudy(
            @RequestBody @Valid AdditionalSelfStudySetRequest request
    ) {
        additionalSelfStudyService.setAdditionalSelfStudy(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("일별 자습을 추가 설정하였습니다");
    }
}
