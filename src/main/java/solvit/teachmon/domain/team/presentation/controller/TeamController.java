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

/**
 * 팀 관리를 위한 REST API 컨트롤러
 * REST API Controller for managing student teams
 * 
 * <p>팀은 여러 학생들을 그룹으로 묶어 관리하는 기능을 제공합니다.
 * Teams provide functionality to group and manage multiple students together.
 * 
 * <p>주요 기능:
 * Main features:
 * <ul>
 *   <li>팀 검색 (Team search by name)</li>
 *   <li>팀 생성 (Team creation with student members)</li>
 *   <li>팀 수정 (Team update including member changes)</li>
 *   <li>팀 삭제 (Team deletion with cascade to participations)</li>
 * </ul>
 * 
 * @see TeamService
 * @see TeamResponseDto
 */
@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    /**
     * 팀 이름으로 검색
     * Searches teams by name (case-insensitive partial match)
     * 
     * <p>검색어를 포함하는 모든 팀을 반환합니다 (대소문자 무시).
     * Returns all teams whose names contain the query string (case-insensitive).
     * 
     * @param query 검색 키워드 (Search keyword for team name)
     * @return 검색된 팀 목록 (List of teams matching the search query)
     */
    @GetMapping("/search")
    public ResponseEntity<List<TeamResponseDto>> searchTeam(@RequestParam String query) {
        return ResponseEntity.ok(teamService.searchTeamByQuery(query));
    }

    /**
     * 새 팀 생성
     * Creates a new team with specified students
     * 
     * <p>팀 이름과 팀원 학생 ID 목록을 받아 새 팀을 생성합니다.
     * Creates a new team with the given name and list of student IDs.
     * 
     * <p>비즈니스 규칙:
     * Business rules:
     * <ul>
     *   <li>팀 이름은 필수입니다 (Team name is required)</li>
     *   <li>학생 ID는 모두 존재해야 합니다 (All student IDs must exist)</li>
     *   <li>중복된 학생 ID는 허용되지 않습니다 (Duplicate student IDs are not allowed)</li>
     * </ul>
     * 
     * @param requestDto 팀 생성 요청 정보 (팀 이름, 학생 ID 목록) (Team creation request containing name and student IDs)
     * @return 성공 메시지 (Success message)
     */
    @PostMapping
    public ResponseEntity<String> createTeam(@RequestBody TeamCreateRequestDto requestDto) {
        teamService.createTeam(requestDto);
        return ResponseEntity.ok("팀이 성공적으로 생성되었습니다.");
    }

    /**
     * 기존 팀 수정
     * Updates an existing team
     * 
     * <p>팀 이름 및/또는 팀원을 변경합니다. 팀원 업데이트는 전체 교체 방식입니다.
     * Updates team name and/or members. Member update is a full replacement operation.
     * 
     * <p>주의: 팀원 목록을 업데이트하면 기존 팀원이 모두 제거되고 새 목록으로 대체됩니다.
     * Warning: Updating the student list removes all existing members and replaces them with the new list.
     * 
     * @param requestDto 팀 수정 요청 정보 (팀 ID, 새 이름, 새 학생 ID 목록) (Team update request with ID, new name, and new student IDs)
     * @return 성공 메시지 (Success message)
     */
    @PatchMapping
    public ResponseEntity<String> updateTeam(@RequestBody TeamUpdateRequestDto requestDto) {
        teamService.updateTeam(requestDto);
        return ResponseEntity.ok("팀이 성공적으로 수정되었습니다.");
    }

    /**
     * 팀 삭제
     * Deletes a team
     * 
     * <p>팀과 관련된 TeamParticipation 레코드도 함께 삭제됩니다 (cascade).
     * The team and associated TeamParticipation records are deleted (cascade).
     * 
     * <p>주의: 학생 엔티티는 삭제되지 않고 팀과의 관계만 제거됩니다.
     * Note: Student entities are not deleted, only their association with the team is removed.
     * 
     * @param requestDto 팀 삭제 요청 정보 (팀 ID) (Team deletion request containing team ID)
     * @return 성공 메시지 (Success message)
     */
    @DeleteMapping
    public ResponseEntity<String> deleteTeam(@RequestBody TeamDeleteRequestDto requestDto) {
        teamService.deleteTeam(requestDto);
        return ResponseEntity.ok("팀이 성공적으로 삭제되었습니다.");
    }
}
