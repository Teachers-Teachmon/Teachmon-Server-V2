package solvit.teachmon.domain.team.domain.repository.querydsl;

import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.team.presentation.dto.response.TeamResponseDto;

import java.util.List;

@Repository
public interface TeamQueryDslRepository {
    List<TeamResponseDto> searchTeamByQuery(String query);
}
