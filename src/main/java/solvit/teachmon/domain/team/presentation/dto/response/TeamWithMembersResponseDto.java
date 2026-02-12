package solvit.teachmon.domain.team.presentation.dto.response;

import java.util.List;

public record TeamWithMembersResponseDto(
        Long id,
        String name,
        List<TeamMemberDto> members
) { }