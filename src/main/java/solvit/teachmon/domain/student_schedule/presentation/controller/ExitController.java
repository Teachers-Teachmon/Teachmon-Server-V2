package solvit.teachmon.domain.student_schedule.presentation.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.student_schedule.application.service.ExitService;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.ExitHistoryResponse;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/exit")
@RequiredArgsConstructor
public class ExitController {
    private final ExitService exitService;

    @GetMapping("/history/week")
    public ResponseEntity<List<ExitHistoryResponse>> getWeekExitHistory() {
        List<ExitHistoryResponse> results = exitService.getWeekExitHistory();

        return ResponseEntity
                .ok()
                .body(results);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ExitHistoryResponse>> getExitHistoryByDay(
            @RequestParam("day") @NotNull(message = "날짜별 이탈 학생 조회에서 day(날짜)는 필수입니다.") LocalDate day
    ) {
        List<ExitHistoryResponse> results = exitService.getExitHistoryByDay(day);

        return ResponseEntity
                .ok()
                .body(results);
    }

    @DeleteMapping("/{exit_id}")
    public ResponseEntity<String> deleteExit(
            @PathVariable("exit_id") @NotNull(message = "이탈 삭제에서 exit_id는 필수입니다.") Long exitId
    ) {
        exitService.deleteExit(exitId);

        return ResponseEntity
                .ok()
                .body("이탈 기록이 삭제되었습니다.");
    }
}
