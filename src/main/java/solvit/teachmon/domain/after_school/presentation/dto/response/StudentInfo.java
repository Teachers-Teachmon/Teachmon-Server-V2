package solvit.teachmon.domain.after_school.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;

@QueryProjection
public record StudentInfo(
        Long id,
        Integer number,
        String name
) {
}