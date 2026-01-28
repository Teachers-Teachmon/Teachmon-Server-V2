package solvit.teachmon.domain.team.domain.repository.querydsl;

import solvit.teachmon.domain.team.presentation.dto.response.TeamResponseDto;

import java.util.List;

public interface TeamQueryDslRepository {
    List<TeamResponseDto> searchTeamByQuery(String query);
}
