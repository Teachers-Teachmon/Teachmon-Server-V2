package solvit.teachmon.domain.team.domain.repository.querydsl;

import solvit.teachmon.domain.team.presentation.dto.response.TeamWithMembersResponseDto;

import java.util.List;

public interface TeamQueryDslRepository {
    List<TeamWithMembersResponseDto> searchTeamsByKeyword(String keyword);
}
