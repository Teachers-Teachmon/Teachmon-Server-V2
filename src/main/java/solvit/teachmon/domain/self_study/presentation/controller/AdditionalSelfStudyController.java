package solvit.teachmon.domain.self_study.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.self_study.application.service.AdditionalSelfStudyService;
import solvit.teachmon.domain.self_study.presentation.dto.request.AdditionalSelfStudySetRequest;
import solvit.teachmon.domain.self_study.presentation.dto.response.AdditionalSelfStudyGetResponse;

import java.util.List;

@Validated
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

    @GetMapping
    public ResponseEntity<List<AdditionalSelfStudyGetResponse>> getAdditionalSelfStudy(
            @NotNull(message = "일별 자습 추가 조회에서 year(년도)는 필수입니다.")
            @RequestParam("year") Integer year
    ) {
        List<AdditionalSelfStudyGetResponse> results = additionalSelfStudyService.getAdditionalSelfStudy(year);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(results);
    }

    @DeleteMapping("/{additional_id}")
    public ResponseEntity<String> deleteAdditionalSelfStudy(
            @PathVariable("additional_id") @Min(value = 1, message = "일별 자습 삭제에서 additional_id(추가 자습 ID)는 1 이상이어야 합니다.") Long additionalId
    ) {
        additionalSelfStudyService.deleteAdditionalSelfStudy(additionalId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("일별 자습을 삭제하였습니다");
    }
}
