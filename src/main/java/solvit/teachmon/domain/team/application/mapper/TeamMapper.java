package solvit.teachmon.domain.team.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import solvit.teachmon.domain.team.domain.entity.TeamEntity;
import solvit.teachmon.domain.team.domain.entity.TeamParticipationEntity;
import solvit.teachmon.domain.team.presentation.dto.response.TeamMemberDto;
import solvit.teachmon.domain.team.presentation.dto.response.TeamResponseDto;
import solvit.teachmon.domain.team.presentation.dto.response.TeamWithMembersResponseDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    TeamResponseDto toResponseDto(TeamEntity teamEntity);
    
    @Mapping(target = "members", source = "teamParticipationList")
    TeamWithMembersResponseDto toWithMembersResponseDto(TeamEntity teamEntity);
    
    @Mapping(target = "id", source = "student.id")
    @Mapping(target = "number", source = "student.number")
    @Mapping(target = "name", source = "student.name")
    @Mapping(target = "grade", source = "student.grade")
    @Mapping(target = "classNumber", source = "student.classNumber")
    TeamMemberDto toTeamMemberDto(TeamParticipationEntity teamParticipation);
    
    default List<TeamMemberDto> mapTeamParticipations(List<TeamParticipationEntity> participations) {
        return participations.stream()
                .map(this::toTeamMemberDto)
                .toList();
    }
}