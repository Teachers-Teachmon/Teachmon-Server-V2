package solvit.teachmon.domain.team.presentation.dto.response;

public record TeamMemberDto(
        Long id,
        Integer number,
        String name,
        Integer grade,
        Integer classNumber
) { }