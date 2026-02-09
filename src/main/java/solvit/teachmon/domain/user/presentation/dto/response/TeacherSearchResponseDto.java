package solvit.teachmon.domain.user.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record TeacherSearchResponseDto(
        Long id,
        String name
) {
    @QueryProjection
    public TeacherSearchResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}