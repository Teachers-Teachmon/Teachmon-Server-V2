package solvit.teachmon.domain.management.student.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record StudentSearchResponseDto(
        Long id,
        Integer grade,
        Integer classNumber,
        Integer number,
        String name
) {
    @QueryProjection
    public StudentSearchResponseDto(Long id, Integer grade, Integer classNumber, Integer number, String name) {
        this.id = id;
        this.grade = grade;
        this.classNumber = classNumber;
        this.number = number;
        this.name = name;
    }
}