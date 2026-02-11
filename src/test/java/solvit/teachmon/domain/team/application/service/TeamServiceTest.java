package solvit.teachmon.domain.team.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import solvit.teachmon.domain.team.application.mapper.TeamMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("팀 서비스 테스트")
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeamMapper teamMapper;

    @Mock
    private StudentEntity student1;

    @Mock
    private StudentEntity student2;

    @Mock
    private TeamEntity team;

    private TeamService teamService;

    @BeforeEach
    void setUp() {
        teamService = new TeamService(teamRepository, teamMapper, studentRepository);
    }

    @Test
    @DisplayName("쿼리로 팀을 검색할 수 있다")
    void shouldSearchTeamByQuerySuccessfully() {
        // Given: 검색 쿼리가 주어졌을 때
        String query = "개발";
        List<TeamResponseDto> expectedResults = List.of(
                new TeamResponseDto(1L, "개발팀"),
                new TeamResponseDto(2L, "개발부서")
        );
        given(teamRepository.searchTeamsByKeyword(query)).willReturn(expectedResults);

        // When: 쿼리로 팀을 검색하면
        List<TeamResponseDto> results = teamService.searchTeamByQuery(query);

        // Then: 검색 결과가 반환된다
        assertThat(results).hasSize(2);
        assertThat(results.get(0).name()).isEqualTo("개발팀");
        assertThat(results.get(1).name()).isEqualTo("개발부서");
        verify(teamRepository).searchTeamsByKeyword(query);
    }

    @Test
    @DisplayName("팀을 생성할 수 있다")
    void shouldCreateTeamSuccessfully() {
        // Given: 팀 생성 요청이 주어졌을 때
        List<Long> studentIds = List.of(1L, 2L);
        TeamCreateRequestDto requestDto = new TeamCreateRequestDto("새로운팀", studentIds);
        
        given(studentRepository.findAllById(studentIds)).willReturn(List.of(student1, student2));
        given(teamRepository.save(any(TeamEntity.class))).willReturn(team);

        // When: 팀을 생성하면
        teamService.createTeam(requestDto);

        // Then: 팀이 생성되고 학생들이 추가된다
        verify(studentRepository).findAllById(studentIds);
        verify(teamRepository).save(any(TeamEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 학생 ID로 팀 생성 시 예외가 발생한다")
    void shouldThrowExceptionWhenCreateTeamWithNonExistentStudent() {
        // Given: 존재하지 않는 학생 ID가 포함된 팀 생성 요청이 주어졌을 때
        List<Long> studentIds = List.of(1L, 2L, 999L);
        TeamCreateRequestDto requestDto = new TeamCreateRequestDto("새로운팀", studentIds);
        
        given(studentRepository.findAllById(studentIds)).willReturn(List.of(student1, student2));

        // When & Then: 팀을 생성하면 예외가 발생한다
        assertThatThrownBy(() -> teamService.createTeam(requestDto))
                .isInstanceOf(StudentNotFoundException.class);
        
        verify(studentRepository).findAllById(studentIds);
        verify(teamRepository, never()).save(any(TeamEntity.class));
    }

    @Test
    @DisplayName("팀 정보를 수정할 수 있다")
    void shouldUpdateTeamSuccessfully() {
        // Given: 팀 수정 요청이 주어졌을 때
        Long teamId = 1L;
        List<TeamUpdateStudentDto> students = List.of(
                new TeamUpdateStudentDto(1L, 1, "김철수"),
                new TeamUpdateStudentDto(2L, 2, "이영희")
        );
        TeamUpdateRequestDto requestDto = new TeamUpdateRequestDto(teamId, "수정된팀명", students);
        
        given(teamRepository.findById(teamId)).willReturn(Optional.of(team));
        given(studentRepository.findAllById(List.of(1L, 2L))).willReturn(List.of(student1, student2));

        // When: 팀을 수정하면
        teamService.updateTeam(requestDto);

        // Then: 팀 정보가 수정된다
        verify(teamRepository).findById(teamId);
        verify(studentRepository).findAllById(List.of(1L, 2L));
        verify(team).updateName("수정된팀명");
    }

    @Test
    @DisplayName("존재하지 않는 팀 ID로 수정 시 예외가 발생한다")
    void shouldThrowExceptionWhenUpdateNonExistentTeam() {
        // Given: 존재하지 않는 팀 ID로 수정 요청이 주어졌을 때
        Long nonExistentTeamId = 999L;
        List<TeamUpdateStudentDto> students = List.of(new TeamUpdateStudentDto(1L, 1, "김철수"));
        TeamUpdateRequestDto requestDto = new TeamUpdateRequestDto(nonExistentTeamId, "수정된팀명", students);
        
        given(teamRepository.findById(nonExistentTeamId)).willReturn(Optional.empty());

        // When & Then: 팀을 수정하면 예외가 발생한다
        assertThatThrownBy(() -> teamService.updateTeam(requestDto))
                .isInstanceOf(TeamNotFoundException.class);
        
        verify(teamRepository).findById(nonExistentTeamId);
        verify(studentRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("존재하지 않는 학생 ID로 팀 수정 시 예외가 발생한다")
    void shouldThrowExceptionWhenUpdateTeamWithNonExistentStudent() {
        // Given: 존재하지 않는 학생 ID가 포함된 팀 수정 요청이 주어졌을 때
        Long teamId = 1L;
        List<TeamUpdateStudentDto> students = List.of(
                new TeamUpdateStudentDto(1L, 1, "김철수"),
                new TeamUpdateStudentDto(999L, 999, "없는학생")
        );
        TeamUpdateRequestDto requestDto = new TeamUpdateRequestDto(teamId, "수정된팀명", students);
        
        given(teamRepository.findById(teamId)).willReturn(Optional.of(team));
        given(studentRepository.findAllById(List.of(1L, 999L))).willReturn(List.of(student1));

        // When & Then: 팀을 수정하면 예외가 발생한다
        assertThatThrownBy(() -> teamService.updateTeam(requestDto))
                .isInstanceOf(StudentNotFoundException.class);
        
        verify(teamRepository).findById(teamId);
        verify(studentRepository).findAllById(List.of(1L, 999L));
    }

    @Test
    @DisplayName("팀을 삭제할 수 있다")
    void shouldDeleteTeamSuccessfully() {
        // Given: 팀 삭제 요청이 주어졌을 때
        Long teamId = 1L;
        TeamDeleteRequestDto requestDto = new TeamDeleteRequestDto(teamId);
        
        given(teamRepository.findById(teamId)).willReturn(Optional.of(team));

        // When: 팀을 삭제하면
        teamService.deleteTeam(requestDto);

        // Then: 팀이 삭제된다
        verify(teamRepository).findById(teamId);
        verify(teamRepository).delete(team);
    }

    @Test
    @DisplayName("존재하지 않는 팀 ID로 삭제 시 예외가 발생한다")
    void shouldThrowExceptionWhenDeleteNonExistentTeam() {
        // Given: 존재하지 않는 팀 ID로 삭제 요청이 주어졌을 때
        Long nonExistentTeamId = 999L;
        TeamDeleteRequestDto requestDto = new TeamDeleteRequestDto(nonExistentTeamId);
        
        given(teamRepository.findById(nonExistentTeamId)).willReturn(Optional.empty());

        // When & Then: 팀을 삭제하면 예외가 발생한다
        assertThatThrownBy(() -> teamService.deleteTeam(requestDto))
                .isInstanceOf(TeamNotFoundException.class);
        
        verify(teamRepository).findById(nonExistentTeamId);
        verify(teamRepository, never()).delete(any(TeamEntity.class));
    }

    @Test
    @DisplayName("빈 학생 리스트로 팀을 생성할 수 있다")
    void shouldCreateTeamWithEmptyStudentList() {
        // Given: 학생이 없는 팀 생성 요청이 주어졌을 때
        List<Long> emptyStudentIds = List.of();
        TeamCreateRequestDto requestDto = new TeamCreateRequestDto("빈팀", emptyStudentIds);
        
        given(studentRepository.findAllById(emptyStudentIds)).willReturn(List.of());
        given(teamRepository.save(any(TeamEntity.class))).willReturn(team);

        // When: 팀을 생성하면
        teamService.createTeam(requestDto);

        // Then: 학생이 없는 팀이 생성된다
        verify(studentRepository).findAllById(emptyStudentIds);
        verify(teamRepository).save(any(TeamEntity.class));
    }

    @Test
    @DisplayName("팀 수정 시 모든 기존 학생을 제거하고 새 학생들을 추가한다")
    void shouldReplaceAllStudentsWhenUpdateTeam() {
        // Given: 기존에 학생이 있는 팀과 새로운 학생 리스트가 주어졌을 때
        Long teamId = 1L;
        List<TeamUpdateStudentDto> newStudents = List.of(
                new TeamUpdateStudentDto(1L, 1, "김철수"),
                new TeamUpdateStudentDto(2L, 2, "이영희")
        );
        TeamUpdateRequestDto requestDto = new TeamUpdateRequestDto(teamId, "수정된팀명", newStudents);
        
        given(teamRepository.findById(teamId)).willReturn(Optional.of(team));
        given(studentRepository.findAllById(List.of(1L, 2L))).willReturn(List.of(student1, student2));

        // When: 팀을 수정하면
        teamService.updateTeam(requestDto);

        // Then: 기존 학생들이 모두 제거되고 새 학생들만 남는다
        verify(teamRepository).findById(teamId);
        verify(studentRepository).findAllById(List.of(1L, 2L));
        verify(team).removeAllStudents();
        verify(team).addStudent(student1);
        verify(team).addStudent(student2);
    }

    @Test
    @DisplayName("모든 팀을 조회할 수 있다")
    void shouldFindAllTeamsSuccessfully() {
        // Given: 팀 엔티티들이 주어졌을 때
        TeamEntity team1 = TeamEntity.builder().name("개발팀").build();
        TeamEntity team2 = TeamEntity.builder().name("디자인팀").build();
        List<TeamEntity> teamEntities = List.of(team1, team2);

        TeamResponseDto responseDto1 = new TeamResponseDto(1L, "개발팀");
        TeamResponseDto responseDto2 = new TeamResponseDto(2L, "디자인팀");

        given(teamRepository.findAll()).willReturn(teamEntities);
        given(teamMapper.toResponseDto(team1)).willReturn(responseDto1);
        given(teamMapper.toResponseDto(team2)).willReturn(responseDto2);

        // When: 모든 팀을 조회하면
        List<TeamResponseDto> results = teamService.findAllTeams();

        // Then: 모든 팀이 반환된다
        assertThat(results).hasSize(2);
        assertThat(results.get(0).name()).isEqualTo("개발팀");
        assertThat(results.get(1).name()).isEqualTo("디자인팀");
        verify(teamRepository).findAll();
        verify(teamMapper).toResponseDto(team1);
        verify(teamMapper).toResponseDto(team2);
    }

    @Test
    @DisplayName("팀이 없을 때 빈 리스트를 반환한다")
    void shouldReturnEmptyListWhenNoTeamsFound() {
        // Given: 팀이 없을 때
        List<TeamEntity> emptyList = List.of();
        given(teamRepository.findAll()).willReturn(emptyList);

        // When: 모든 팀을 조회하면
        List<TeamResponseDto> results = teamService.findAllTeams();

        // Then: 빈 리스트가 반환된다
        assertThat(results).isEmpty();
        verify(teamRepository).findAll();
    }
}
