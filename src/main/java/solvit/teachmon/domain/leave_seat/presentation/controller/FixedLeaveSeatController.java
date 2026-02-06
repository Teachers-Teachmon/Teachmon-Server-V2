package solvit.teachmon.domain.leave_seat.presentation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.leave_seat.application.facade.FixedLeaveSeatFacadeService;
import solvit.teachmon.domain.leave_seat.presentation.dto.request.FixedLeaveSeatCreateRequest;
import solvit.teachmon.domain.leave_seat.presentation.dto.request.FixedLeaveSeatUpdateRequest;
import solvit.teachmon.domain.leave_seat.presentation.dto.response.FixedLeaveSeatDetailResponse;
import solvit.teachmon.domain.leave_seat.presentation.dto.response.FixedLeaveSeatListResponse;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.global.security.user.TeachmonUserDetails;

import java.util.List;

@Validated
@RestController
@RequestMapping("/leaveseat/static")
@RequiredArgsConstructor
public class FixedLeaveSeatController {
    private final FixedLeaveSeatFacadeService fixedLeaveSeatFacadeService;

    @PostMapping
    public ResponseEntity<String> createStaticLeaveSeat(
            @RequestBody @Valid FixedLeaveSeatCreateRequest request,
            @AuthenticationPrincipal TeachmonUserDetails teachmonUserDetails
    ) {
        TeacherEntity teacher = teachmonUserDetails.teacherEntity();

        fixedLeaveSeatFacadeService.createStaticLeaveSeat(request, teacher);

        return ResponseEntity
                .ok()
                .body("고정 이석을 작성하였습니다.");
    }

    @GetMapping
    public ResponseEntity<List<FixedLeaveSeatListResponse>> getStaticLeaveSeatList() {
        List<FixedLeaveSeatListResponse> results = fixedLeaveSeatFacadeService.getStaticLeaveSeatList();

        return ResponseEntity
                .ok()
                .body(results);
    }

    @GetMapping("/{static_leaveseat_id}")
    public ResponseEntity<FixedLeaveSeatDetailResponse> getStaticLeaveSeatDetail(
            @PathVariable("static_leaveseat_id") @Positive(message = "고정 이석 상세 조회에서 static_leaveseat_id(고정 이석 ID)는 양수여야 합니다.") Long staticLeaveSeatId
    ) {
        FixedLeaveSeatDetailResponse result = fixedLeaveSeatFacadeService.getStaticLeaveSeatDetail(staticLeaveSeatId);

        return ResponseEntity
                .ok()
                .body(result);
    }

    @PatchMapping("/{static_leaveseat_id}")
    public ResponseEntity<String> updateStaticLeaveSeat(
            @PathVariable("static_leaveseat_id") @Positive(message = "고정 이석 수정에서 static_leaveseat_id(고정 이석 ID)는 양수여야 합니다.") Long staticLeaveSeatId,
            @Valid @RequestBody FixedLeaveSeatUpdateRequest request,
            @AuthenticationPrincipal TeachmonUserDetails teachmonUserDetails
    ) {
        TeacherEntity teacher = teachmonUserDetails.teacherEntity();

        fixedLeaveSeatFacadeService.updateStaticLeaveSeat(staticLeaveSeatId, request, teacher);

        return ResponseEntity
                .ok()
                .body("고정 이석을 수정하였습니다.");
    }

    @DeleteMapping("/{static_leaveseat_id}")
    public ResponseEntity<String> deleteStaticLeaveSeat(
            @PathVariable("static_leaveseat_id") @Positive(message = "고정 이석 삭제에서 static_leaveseat_id(고정 이석 ID)는 양수여야 합니다.") Long staticLeaveSeatId
    ) {
        fixedLeaveSeatFacadeService.deleteStaticLeaveSeat(staticLeaveSeatId);

        return ResponseEntity
                .ok()
                .body("고정 이석 삭제가 완료되었습니다");
    }
}
