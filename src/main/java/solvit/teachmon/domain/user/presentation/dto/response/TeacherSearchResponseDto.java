package solvit.teachmon.domain.user.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;

@QueryProjection
public record TeacherSearchResponseDto(
        Long id,
        String name
) {
}