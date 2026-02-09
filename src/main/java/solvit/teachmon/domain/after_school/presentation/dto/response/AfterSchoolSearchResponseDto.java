package solvit.teachmon.domain.after_school.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;

@QueryProjection
public record AfterSchoolSearchResponseDto(
        Long id,
        String name
) {
}