package solvit.teachmon.domain.team.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.team.application.service.TeamService;
import solvit.teachmon.domain.team.presentation.dto.request.TeamCreateRequestDto;
import solvit.teachmon.domain.team.presentation.dto.request.TeamDeleteRequestDto;
import solvit.teachmon.domain.team.presentation.dto.request.TeamUpdateRequestDto;
import solvit.teachmon.domain.team.presentation.dto.response.TeamResponseDto;

import java.util.List;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @GetMapping("/search")
    public ResponseEntity<List<TeamResponseDto>> searchTeam(@RequestParam String query) {
        return ResponseEntity.ok(teamService.searchTeamByQuery(query));
    }

    @PostMapping
    public ResponseEntity<String> createTeam(@RequestBody TeamCreateRequestDto requestDto) {
        teamService.createTeam(requestDto);
        return ResponseEntity.ok("팀이 성공적으로 생성되었습니다.");
    }

    @PatchMapping
    public ResponseEntity<String> updateTeam(@RequestBody TeamUpdateRequestDto requestDto) {
        teamService.updateTeam(requestDto);
        return ResponseEntity.ok("팀이 성공적으로 수정되었습니다.");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteTeam(@RequestBody TeamDeleteRequestDto requestDto) {
        teamService.deleteTeam(requestDto);
        return ResponseEntity.ok("팀이 성공적으로 삭제되었습니다.");
    }
}
