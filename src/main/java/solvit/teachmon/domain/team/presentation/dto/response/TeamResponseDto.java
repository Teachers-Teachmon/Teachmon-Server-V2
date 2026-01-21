package solvit.teachmon.domain.team.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;

@QueryProjection
public record TeamResponseDto(
        Long id,
        String name
) { }
