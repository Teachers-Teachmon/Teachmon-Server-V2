package solvit.teachmon.domain.after_school.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record AfterSchoolSearchResponseDto(
        Long id,
        String name
) {
    @QueryProjection
    public AfterSchoolSearchResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}