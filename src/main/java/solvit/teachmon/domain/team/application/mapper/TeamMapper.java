package solvit.teachmon.domain.team.application.mapper;

import org.mapstruct.Mapper;
import solvit.teachmon.domain.team.domain.entity.TeamEntity;
import solvit.teachmon.domain.team.presentation.dto.response.TeamResponseDto;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    TeamResponseDto toResponseDto(TeamEntity teamEntity);
}