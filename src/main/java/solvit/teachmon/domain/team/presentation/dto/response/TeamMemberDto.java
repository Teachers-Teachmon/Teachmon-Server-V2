package solvit.teachmon.domain.team.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;

@QueryProjection
public record TeamMemberDto(
        Long id,
        Integer number,
        String name,
        Integer grade,
        Integer classNumber
) {
}