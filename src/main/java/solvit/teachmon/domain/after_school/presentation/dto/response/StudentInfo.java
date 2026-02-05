package solvit.teachmon.domain.after_school.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record StudentInfo(
        Integer number,
        String name
) {
    @QueryProjection
    public StudentInfo(Integer number, String name) {
        this.number = number;
        this.name = name;
    }
}