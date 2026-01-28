package solvit.teachmon.domain.team.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.team.exception.InvalidTeamInfoException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("팀 엔티티 테스트")
class TeamEntityTest {

    @Mock
    private StudentEntity student1;

    @Mock
    private StudentEntity student2;

    @Mock
    private StudentEntity student3;

    @Test
    @DisplayName("팀을 생성할 수 있다")
    void shouldCreateTeamSuccessfully() {
        // Given: 팀 이름이 주어졌을 때
        String teamName = "개발팀";

        // When: 팀을 생성하면
        TeamEntity team = TeamEntity.builder()
                .name(teamName)
                .build();

        // Then: 팀이 올바르게 생성된다
        assertThat(team.getName()).isEqualTo(teamName);
        assertThat(team.getTeamParticipationList()).isEmpty();
    }

    @Test
    @DisplayName("팀 이름이 null이면 InvalidTeamInfoException이 발생한다")
    void shouldThrowExceptionWhenNameIsNull() {
        // Given: 팀 이름이 null인 상황

        // When & Then: 팀을 생성하면 예외가 발생한다
        assertThatThrownBy(() -> TeamEntity.builder()
                .name(null)
                .build())
                .isInstanceOf(InvalidTeamInfoException.class)
                .hasMessage("이름은 비어 있을 수 없습니다.");
    }

    @Test
    @DisplayName("팀에 학생을 추가할 수 있다")
    void shouldAddStudentToTeamSuccessfully() {
        // Given: 팀이 주어졌을 때
        TeamEntity team = TeamEntity.builder()
                .name("개발팀")
                .build();

        // When: 팀에 학생을 추가하면
        team.addStudent(student1);

        // Then: 학생이 팀에 추가된다
        assertThat(team.getTeamParticipationList()).hasSize(1);
        assertThat(team.getTeamParticipationList().get(0).getStudent()).isEqualTo(student1);
        assertThat(team.getTeamParticipationList().get(0).getTeam()).isEqualTo(team);
    }

    @Test
    @DisplayName("팀에 여러 학생을 추가할 수 있다")
    void shouldAddMultipleStudentsToTeamSuccessfully() {
        // Given: 팀과 여러 학생이 주어졌을 때
        TeamEntity team = TeamEntity.builder()
                .name("개발팀")
                .build();
        given(student1.getName()).willReturn("김철수");
        given(student2.getName()).willReturn("이영희");
        given(student3.getName()).willReturn("박민수");

        // When: 팀에 여러 학생을 추가하면
        team.addStudent(student1);
        team.addStudent(student2);
        team.addStudent(student3);

        // Then: 모든 학생이 팀에 추가된다
        assertThat(team.getTeamParticipationList()).hasSize(3);
        assertThat(team.getTeamParticipationList())
                .extracting(participation -> participation.getStudent().getName())
                .containsExactly("김철수", "이영희", "박민수");
    }

    @Test
    @DisplayName("팀에서 학생을 제거할 수 있다")
    void shouldRemoveStudentFromTeamSuccessfully() {
        // Given: 팀에 학생이 있을 때
        TeamEntity team = TeamEntity.builder()
                .name("개발팀")
                .build();
        team.addStudent(student1);
        team.addStudent(student2);

        // When: 팀에서 학생을 제거하면
        team.removeStudent(student1);

        // Then: 해당 학생이 팀에서 제거된다
        assertThat(team.getTeamParticipationList()).hasSize(1);
        assertThat(team.getTeamParticipationList().get(0).getStudent()).isEqualTo(student2);
    }

    @Test
    @DisplayName("팀에 참여하지 않은 학생을 제거하려 하면 예외가 발생한다")
    void shouldThrowExceptionWhenRemoveNonParticipatingStudent() {
        // Given: 팀에 학생1만 참여하고 있을 때
        TeamEntity team = TeamEntity.builder()
                .name("개발팀")
                .build();
        team.addStudent(student1);

        // When & Then: 참여하지 않은 학생2를 제거하려 하면 예외가 발생한다
        assertThatThrownBy(() -> team.removeStudent(student2))
                .isInstanceOf(InvalidTeamInfoException.class)
                .hasMessage("팀에 참여하지 않은 학생입니다.");
    }

    @Test
    @DisplayName("팀에서 모든 학생을 제거할 수 있다")
    void shouldRemoveAllStudentsFromTeamSuccessfully() {
        // Given: 팀에 여러 학생이 있을 때
        TeamEntity team = TeamEntity.builder()
                .name("개발팀")
                .build();
        team.addStudent(student1);
        team.addStudent(student2);
        team.addStudent(student3);

        // When: 모든 학생을 제거하면
        team.removeAllStudents();

        // Then: 팀에 학생이 없어진다
        assertThat(team.getTeamParticipationList()).isEmpty();
    }

    @Test
    @DisplayName("팀 이름을 변경할 수 있다")
    void shouldUpdateTeamNameSuccessfully() {
        // Given: 팀이 있을 때
        TeamEntity team = TeamEntity.builder()
                .name("기존팀명")
                .build();
        String newName = "새로운팀명";

        // When: 팀 이름을 변경하면
        team.updateName(newName);

        // Then: 팀 이름이 변경된다
        assertThat(team.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("팀 이름을 null로 변경하려 하면 예외가 발생한다")
    void shouldThrowExceptionWhenUpdateNameToNull() {
        // Given: 팀이 있을 때
        TeamEntity team = TeamEntity.builder()
                .name("기존팀명")
                .build();

        // When & Then: 팀 이름을 null로 변경하려 하면 예외가 발생한다
        assertThatThrownBy(() -> team.updateName(null))
                .isInstanceOf(InvalidTeamInfoException.class)
                .hasMessage("이름은 비어 있을 수 없습니다.");
    }
}
