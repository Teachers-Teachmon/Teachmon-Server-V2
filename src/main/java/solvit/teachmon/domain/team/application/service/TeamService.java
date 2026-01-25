package solvit.teachmon.domain.team.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.management.student.exception.StudentNotFoundException;
import solvit.teachmon.domain.team.domain.entity.TeamEntity;
import solvit.teachmon.domain.team.domain.repository.TeamRepository;
import solvit.teachmon.domain.team.exception.TeamNotFoundException;
import solvit.teachmon.domain.team.presentation.dto.request.TeamCreateRequestDto;
import solvit.teachmon.domain.team.presentation.dto.request.TeamDeleteRequestDto;
import solvit.teachmon.domain.team.presentation.dto.request.TeamUpdateRequestDto;
import solvit.teachmon.domain.team.presentation.dto.request.TeamUpdateStudentDto;
import solvit.teachmon.domain.team.presentation.dto.response.TeamResponseDto;

import java.util.List;

/**
 * 팀 관리 비즈니스 로직을 처리하는 서비스
 * Service for handling team management business logic
 * 
 * <p>팀과 학생 간의 다대다 관계를 TeamParticipation 조인 엔티티를 통해 관리합니다.
 * Manages many-to-many relationship between teams and students through TeamParticipation join entity.
 * 
 * @see TeamEntity
 * @see TeamRepository
 */
@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final StudentRepository studentRepository;

    /**
     * 팀 이름으로 검색 (대소문자 무시, 부분 일치)
     * Searches teams by name (case-insensitive, partial match)
     * 
     * @param query 검색 키워드 (Search keyword)
     * @return 검색된 팀 목록 (List of matching teams)
     */
    public List<TeamResponseDto> searchTeamByQuery(String query) {
        return teamRepository.findTeamsBySearchKeyword(query);
    }

    /**
     * 새 팀 생성
     * Creates a new team
     * 
     * <p>팀 생성 과정:
     * Team creation process:
     * <ol>
     *   <li>학생 ID 유효성 검증 (모든 ID가 존재하는지 확인) (Validate student IDs - all must exist)</li>
     *   <li>팀 엔티티 생성 및 저장 (Create and save team entity)</li>
     *   <li>팀-학생 관계(TeamParticipation) 생성 (Create team-student relationships)</li>
     * </ol>
     * 
     * @param requestDto 팀 생성 요청 (팀 이름, 학생 ID 목록) (Team creation request)
     * @throws StudentNotFoundException 존재하지 않는 학생 ID가 포함된 경우 (If any student ID doesn't exist)
     */
    @Transactional
    public void createTeam(TeamCreateRequestDto requestDto) {
        List<StudentEntity> students = validateAndGetStudents(requestDto.students());
        
        TeamEntity team = TeamEntity.builder()
                .name(requestDto.name())
                .build();
        
        TeamEntity savedTeam = teamRepository.save(team);
        addStudentsToTeam(savedTeam, students);
    }

    /**
     * 팀 정보 수정 (팀 이름 및 팀원)
     * Updates team information (name and members)
     * 
     * <p>중요: 팀원 업데이트는 "전체 교체" 방식입니다.
     * Important: Member update is a "full replacement" operation.
     * 
     * <p>업데이트 과정:
     * Update process:
     * <ol>
     *   <li>팀 존재 여부 확인 (Verify team exists)</li>
     *   <li>팀 이름 수정 (Update team name)</li>
     *   <li>기존 팀원 모두 제거 (removeAllStudents) (Remove all existing members)</li>
     *   <li>새 팀원 추가 (addStudentsToTeam) (Add new members)</li>
     * </ol>
     * 
     * <p>orphanRemoval=true 설정으로 인해 removeAllStudents() 호출 시 
     * TeamParticipation 엔티티가 자동으로 삭제됩니다.
     * Due to orphanRemoval=true, TeamParticipation entities are automatically deleted when removeAllStudents() is called.
     * 
     * @param requestDto 팀 수정 요청 (팀 ID, 새 이름, 새 학생 ID 목록) (Team update request)
     * @throws TeamNotFoundException 팀이 존재하지 않는 경우 (If team doesn't exist)
     * @throws StudentNotFoundException 존재하지 않는 학생 ID가 포함된 경우 (If any student ID doesn't exist)
     */
    @Transactional
    public void updateTeam(TeamUpdateRequestDto requestDto) {
        TeamEntity team = teamRepository.findById(requestDto.id())
                .orElseThrow(TeamNotFoundException::new);
        
        team.updateName(requestDto.name());
        
        List<Long> studentIds = extractStudentIds(requestDto.students());
        List<StudentEntity> students = validateAndGetStudents(studentIds);
        
        team.removeAllStudents();
        addStudentsToTeam(team, students);
    }

    /**
     * 학생 ID 유효성 검증 및 엔티티 조회
     * Validates student IDs and retrieves student entities
     * 
     * <p>모든 학생 ID가 데이터베이스에 존재하는지 확인합니다.
     * Verifies that all student IDs exist in the database.
     * 
     * <p>검증 로직: findAllById로 조회한 결과의 크기가 요청한 ID 개수와 다르면 
     * 일부 ID가 존재하지 않는 것으로 판단합니다.
     * Validation logic: If the size of results from findAllById differs from the requested ID count,
     * it means some IDs don't exist.
     * 
     * @param studentIds 검증할 학생 ID 목록 (Student IDs to validate)
     * @return 검증된 학생 엔티티 목록 (Validated student entities)
     * @throws StudentNotFoundException 일부 학생 ID가 존재하지 않는 경우 (If any student ID doesn't exist)
     */
    private List<StudentEntity> validateAndGetStudents(List<Long> studentIds) {
        List<StudentEntity> students = studentRepository.findAllById(studentIds);
        if (students.size() != studentIds.size()) {
            throw new StudentNotFoundException();
        }
        return students;
    }

    /**
     * 팀에 학생들을 추가
     * Adds students to a team
     * 
     * <p>각 학생에 대해 TeamParticipation 조인 엔티티를 생성하여 팀-학생 관계를 맺습니다.
     * Creates TeamParticipation join entities for each student to establish team-student relationships.
     * 
     * @param team 학생을 추가할 팀 (Team to add students to)
     * @param students 추가할 학생 목록 (Students to add)
     */
    private void addStudentsToTeam(TeamEntity team, List<StudentEntity> students) {
        students.forEach(team::addStudent);
    }

    /**
     * 팀 삭제
     * Deletes a team
     * 
     * <p>팀 삭제 시 cascade 설정으로 인해 관련 TeamParticipation 레코드도 함께 삭제됩니다.
     * When deleting a team, related TeamParticipation records are also deleted due to cascade settings.
     * 
     * <p>주의: 학생 엔티티는 삭제되지 않고 팀과의 관계만 제거됩니다.
     * Note: Student entities are not deleted, only their relationships with the team are removed.
     * 
     * @param requestDto 팀 삭제 요청 (팀 ID) (Team deletion request)
     * @throws TeamNotFoundException 팀이 존재하지 않는 경우 (If team doesn't exist)
     */
    @Transactional
    public void deleteTeam(TeamDeleteRequestDto requestDto) {
        TeamEntity team = teamRepository.findById(requestDto.id())
                .orElseThrow(TeamNotFoundException::new);
        
        teamRepository.delete(team);
    }

    /**
     * DTO 목록에서 학생 ID 추출
     * Extracts student IDs from DTO list
     * 
     * @param students 학생 DTO 목록 (Student DTOs)
     * @return 학생 ID 목록 (Student IDs)
     */
    private List<Long> extractStudentIds(List<TeamUpdateStudentDto> students) {
        return students.stream()
                .map(TeamUpdateStudentDto::id)
                .toList();
    }
}
