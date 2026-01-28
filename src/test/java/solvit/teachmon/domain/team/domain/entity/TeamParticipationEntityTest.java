package solvit.teachmon.domain.team.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.team.exception.InvalidTeamParticipationInfoException;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("팀 참여 엔티티 테스트")
class TeamParticipationEntityTest {

    @Mock
    private TeamEntity team;

    @Mock
    private StudentEntity student;

    @Test
    @DisplayName("팀 참여를 생성할 수 있다")
    void shouldCreateTeamParticipationSuccessfully() {
        // Given: 팀과 학생이 주어졌을 때

        // When: 팀 참여를 생성하면
        TeamParticipationEntity participation = TeamParticipationEntity.builder()
                .team(team)
                .student(student)
                .build();

        // Then: 팀 참여가 올바르게 생성된다
        assertThat(participation.getTeam()).isEqualTo(team);
        assertThat(participation.getStudent()).isEqualTo(student);
    }

    @Test
    @DisplayName("팀이 null이면 InvalidTeamParticipationInfoException이 발생한다")
    void shouldThrowExceptionWhenTeamIsNull() {
        // Given: 팀이 null인 상황

        // When & Then: 팀 참여를 생성하면 예외가 발생한다
        assertThatThrownBy(() -> TeamParticipationEntity.builder()
                .team(null)
                .student(student)
                .build())
                .isInstanceOf(InvalidTeamParticipationInfoException.class)
                .hasMessage("팀은 비어 있을 수 없습니다.");
    }

    @Test
    @DisplayName("학생이 null이면 InvalidTeamParticipationInfoException이 발생한다")
    void shouldThrowExceptionWhenStudentIsNull() {
        // Given: 학생이 null인 상황

        // When & Then: 팀 참여를 생성하면 예외가 발생한다
        assertThatThrownBy(() -> TeamParticipationEntity.builder()
                .team(team)
                .student(null)
                .build())
                .isInstanceOf(InvalidTeamParticipationInfoException.class)
                .hasMessage("학생은 비어 있을 수 없습니다.");
    }

    @Test
    @DisplayName("팀과 학생이 모두 null이면 팀 검증에서 먼저 예외가 발생한다")
    void shouldThrowExceptionForTeamFirstWhenBothAreNull() {
        // Given: 팀과 학생이 모두 null인 상황

        // When & Then: 팀 참여를 생성하면 팀 검증에서 먼저 예외가 발생한다
        assertThatThrownBy(() -> TeamParticipationEntity.builder()
                .team(null)
                .student(null)
                .build())
                .isInstanceOf(InvalidTeamParticipationInfoException.class)
                .hasMessage("팀은 비어 있을 수 없습니다.");
    }
}
