package solvit.teachmon.domain.team.application.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.team.domain.entity.TeamEntity;
import solvit.teachmon.domain.team.domain.entity.TeamParticipationEntity;
import solvit.teachmon.domain.team.presentation.dto.response.TeamMemberDto;
import solvit.teachmon.domain.team.presentation.dto.response.TeamWithMembersResponseDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("팀 매퍼 테스트")
class TeamMapperTest {

    private final TeamMapper teamMapper = Mappers.getMapper(TeamMapper.class);


    @Test
    @DisplayName("TeamEntity를 TeamWithMembersResponseDto로 변환할 수 있다")
    void shouldConvertTeamEntityToTeamWithMembersResponseDto() {
        // Given: 팀과 멤버가 있는 TeamEntity가 주어졌을 때
        StudentEntity student1 = createMockStudent(1L, 1, "김철수", 1, 1);
        StudentEntity student2 = createMockStudent(2L, 2, "이영희", 1, 2);
        
        TeamParticipationEntity participation1 = createMockTeamParticipation(student1);
        TeamParticipationEntity participation2 = createMockTeamParticipation(student2);
        
        TeamEntity teamEntity = mock(TeamEntity.class);
        given(teamEntity.getId()).willReturn(1L);
        given(teamEntity.getName()).willReturn("개발팀");
        given(teamEntity.getTeamParticipationList()).willReturn(java.util.List.of(participation1, participation2));

        // When: TeamWithMembersResponseDto로 변환하면
        TeamWithMembersResponseDto result = teamMapper.toWithMembersResponseDto(teamEntity);

        // Then: 팀과 멤버 정보가 올바르게 변환된다
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("개발팀");
        assertThat(result.members()).hasSize(2);
        
        TeamMemberDto member1 = result.members().getFirst();
        assertThat(member1.id()).isEqualTo(1L);
        assertThat(member1.number()).isEqualTo(1);
        assertThat(member1.name()).isEqualTo("김철수");
        assertThat(member1.grade()).isEqualTo(1);
        assertThat(member1.classNumber()).isEqualTo(1);
        
        TeamMemberDto member2 = result.members().get(1);
        assertThat(member2.id()).isEqualTo(2L);
        assertThat(member2.number()).isEqualTo(2);
        assertThat(member2.name()).isEqualTo("이영희");
        assertThat(member2.grade()).isEqualTo(1);
        assertThat(member2.classNumber()).isEqualTo(2);
    }

    @Test
    @DisplayName("TeamParticipationEntity를 TeamMemberDto로 변환할 수 있다")
    void shouldConvertTeamParticipationEntityToTeamMemberDto() {
        // Given: TeamParticipationEntity가 주어졌을 때
        StudentEntity student = createMockStudent(1L, 5, "박민수", 2, 3);
        TeamParticipationEntity participation = createMockTeamParticipation(student);

        // When: TeamMemberDto로 변환하면
        TeamMemberDto result = teamMapper.toTeamMemberDto(participation);

        // Then: 학생 정보가 올바르게 변환된다
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.number()).isEqualTo(5);
        assertThat(result.name()).isEqualTo("박민수");
        assertThat(result.grade()).isEqualTo(2);
        assertThat(result.classNumber()).isEqualTo(3);
    }

    @Test
    @DisplayName("멤버가 없는 팀도 올바르게 변환된다")
    void shouldConvertTeamWithoutMembers() {
        // Given: 멤버가 없는 TeamEntity가 주어졌을 때
        TeamEntity teamEntity = mock(TeamEntity.class);
        given(teamEntity.getId()).willReturn(1L);
        given(teamEntity.getName()).willReturn("빈팀");
        given(teamEntity.getTeamParticipationList()).willReturn(java.util.List.of());

        // When: TeamWithMembersResponseDto로 변환하면
        TeamWithMembersResponseDto result = teamMapper.toWithMembersResponseDto(teamEntity);

        // Then: 빈 멤버 리스트와 함께 올바르게 변환된다
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("빈팀");
        assertThat(result.members()).isEmpty();
    }

    private StudentEntity createMockStudent(Long id, Integer number, String name, Integer grade, Integer classNumber) {
        StudentEntity student = mock(StudentEntity.class);
        given(student.getId()).willReturn(id);
        given(student.getNumber()).willReturn(number);
        given(student.getName()).willReturn(name);
        given(student.getGrade()).willReturn(grade);
        given(student.getClassNumber()).willReturn(classNumber);
        return student;
    }

    private TeamParticipationEntity createMockTeamParticipation(StudentEntity student) {
        TeamParticipationEntity participation = mock(TeamParticipationEntity.class);
        given(participation.getStudent()).willReturn(student);
        return participation;
    }
}