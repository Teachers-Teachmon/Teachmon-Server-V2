package solvit.teachmon.domain.team.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;

@QueryProjection
public record TeamWithMembersResponseDto(
        Long id,
        String name,
        List<TeamMemberDto> members
) { }