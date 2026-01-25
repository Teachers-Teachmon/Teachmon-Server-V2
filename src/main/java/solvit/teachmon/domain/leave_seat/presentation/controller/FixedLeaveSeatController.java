package solvit.teachmon.domain.leave_seat.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import solvit.teachmon.domain.leave_seat.application.facade.FixedLeaveSeatFacadeService;
import solvit.teachmon.domain.leave_seat.presentation.dto.request.FixedLeaveSeatCreateRequest;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;

@Validated
@RestController
@RequestMapping("/leaveseat/static")
@RequiredArgsConstructor
public class FixedLeaveSeatController {
    // TODO: 인증 로직 추가 후, 실제 TeacherRepository 주입 무조건 삭제!!
    private final TeacherRepository teacherRepository;
    private final FixedLeaveSeatFacadeService fixedLeaveSeatFacadeService;

    @PostMapping
    public ResponseEntity<String> createStaticLeaveSeat(
            @RequestBody @Valid FixedLeaveSeatCreateRequest request
    ) {
        // TODO: 인증 로직 추가 후, 실제 TeacherEntity 주입
        TeacherEntity teacher = teacherRepository.findById(1L).orElseThrow();

        fixedLeaveSeatFacadeService.createStaticLeaveSeat(request, teacher);

        return ResponseEntity
                .ok()
                .body("고정 이석을 작성하였습니다.");
    }
}
