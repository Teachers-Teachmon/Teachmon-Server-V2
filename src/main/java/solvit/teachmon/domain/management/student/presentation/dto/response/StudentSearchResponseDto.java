package solvit.teachmon.domain.management.student.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;

@QueryProjection
public record StudentSearchResponseDto(
        Long id,
        Integer grade,
        Integer classNumber,
        Integer number,
        String name
) {
}