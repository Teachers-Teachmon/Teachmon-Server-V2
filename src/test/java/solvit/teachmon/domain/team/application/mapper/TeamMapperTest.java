package solvit.teachmon.domain.team.application.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import solvit.teachmon.domain.team.domain.entity.TeamEntity;
import solvit.teachmon.domain.team.presentation.dto.response.TeamResponseDto;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("팀 매퍼 테스트")
class TeamMapperTest {

    private final TeamMapper teamMapper = Mappers.getMapper(TeamMapper.class);

    @Test
    @DisplayName("TeamEntity를 TeamResponseDto로 변환할 수 있다")
    void shouldConvertTeamEntityToTeamResponseDto() {
        // Given: TeamEntity가 주어졌을 때
        TeamEntity teamEntity = TeamEntity.builder()
                .name("개발팀")
                .build();

        // When: TeamResponseDto로 변환하면
        TeamResponseDto result = teamMapper.toResponseDto(teamEntity);

        // Then: 올바르게 변환된다
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("개발팀");
    }

    @Test
    @DisplayName("null TeamEntity를 변환하면 null을 반환한다")
    void shouldReturnNullWhenTeamEntityIsNull() {
        // Given: null TeamEntity가 주어졌을 때
        TeamEntity teamEntity = null;

        // When: TeamResponseDto로 변환하면
        TeamResponseDto result = teamMapper.toResponseDto(teamEntity);

        // Then: null이 반환된다
        assertThat(result).isNull();
    }
}